import { dirname, join } from 'path';
import * as fs from 'fs';
import * as glob from 'fast-glob';
import * as ts from 'typescript';
import { tsquery } from '@phenomnomnominal/tsquery';

import * as aliases from '@phensley/cldr-core/lib/locale/autogen.aliases';
import * as distance from '@phensley/cldr-core/lib/locale/autogen.distance';
import * as locales from '@phensley/cldr-core/lib/locale/autogen.locales';
import * as partition from '@phensley/cldr-core/lib/locale/autogen.partition';
import * as subtags from '@phensley/cldr-core/lib/locale/autogen.subtags';

import { decode } from './parser';
import { Builder } from './generator';
import { tojava } from './java';
import { escapeWrap } from './utils';
import { CodeBuilder, Origin } from '@phensley/cldr-schema';
import { config as DEFAULT_CONFIG } from '@phensley/cldr/lib/config';

const PACKAGE = 'package com.squarespace.cldrengine.internal;\n\n';

const IGNORE = new Set<string>([
  'SchemaConfig',
  'DateFieldSymbol',
  'DateTimePatternFieldType',
  'Digits',
  'Field',
  'Instruction',
  'PrimitiveBundle',
  'Origin',
  'Scope',
  'ScopeMap',
  'Vector1',
  'Vector2'
]);

export const buildSchema = (origin: Origin) => {
  const builder = new Builder({});
  builder.construct(origin);
  console.log(builder.buf.join(''));
};

const loadSources = (pattern: string): ts.SourceFile[] =>
  glob.sync(pattern)
    .map(f => {
      const p = f.toString();
      return [p, fs.readFileSync(p, { encoding: 'utf-8' })];
    })
    .map(([p, data]) => tsquery.ast(data, p));

const makedirs = (p: string) => {
  if (fs.existsSync(p)) {
    return;
  }
  makedirs(dirname(p));
  fs.mkdirSync(p);
};

export const main = () => {
  const root = join(__dirname, '../node_modules/@phensley/cldr-schema/lib/**/*.d.ts');
  const sources = loadSources(root);
  const enums: any[] = [];
  const interfaces: any[] = [];

  // Collect all interfaces and union types from each source file
  for (const source of sources) {
    tsquery(source, 'InterfaceDeclaration')
      .forEach(e => interfaces.push(decode(e)));
    tsquery(source, 'TypeAliasDeclaration')
      .forEach(e => enums.push(decode(e)));
  }

  let dest = join(__dirname, '../../src/generated/java/com/squarespace/cldrengine/internal');
  makedirs(dest);

  // Generate the schema
  const types = interfaces.reduce((p, c) => {
    p[c.name] = c;
    return p;
  }, {});

  const origin = new CodeBuilder(DEFAULT_CONFIG).origin();

  const builder = new Builder(types);
  builder.construct(origin);
  let code = builder.buf.join('');
  write(join(dest, 'Meta.java'), code);

  // Generate interfaces
  for (const o of interfaces) {
    if (IGNORE.has(o.name)) {
      continue;
    }
    let code = tojava(o, types);
    if (code.indexOf('Map<') !== -1) {
      code = 'import java.util.Map;\n\n' + code;
    }
    code = PACKAGE + code;
    write(join(dest, `${o.name}.java`), code);
  }

  // Generate enums
  for (const o of enums) {
    if (IGNORE.has(o.name)) {
      continue;
    }
    let code = PACKAGE;
    code += tojava(o);
    write(join(dest, `${o.name}.java`), code);
  }

  // Generate constants
  writeConstants(dest, 'LocaleConstants', {
    ...aliases,
    ...distance,
    ...locales,
    ...partition,
    ...subtags
  });

  // Copy packs
  dest = join(__dirname, '../../src/generated/resources/com/squarespace/cldrengine/internal');
  makedirs(dest);

  const packroot = join(__dirname, '../node_modules/@phensley/cldr/packs');
  fs.readdirSync(packroot).filter(p => p.endsWith('.json')).forEach(p => {
    const src = join(packroot, p);
    const dst = join(dest, p);
    fs.copyFileSync(src, dst);
  });
};

const writeConstants = (dest: string, name: string, obj: any) => {
  let code = PACKAGE;
  code += `public class ${name} {\n\n`;
  Object.keys(obj).forEach(k => {
    const key = k.replace(/[^\w]+/g, '_').toUpperCase();
    let val = obj[k];
    val = typeof val === 'string' ? val : JSON.stringify(val);
    code += `  public static final String ${key} =\n`;
    code += '    ' + escapeWrap(val, 100).join(' +\n    ');
    code += ';\n\n';
  });
  code += '\n}\n';
  write(join(dest, `${name}.java`), code);
};

const write = (path: string, data: string) =>
  fs.writeFileSync(path, data, { encoding: 'utf-8' });

main();
