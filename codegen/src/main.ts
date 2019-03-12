import { ORIGIN } from '@phensley/cldr-schema';
import { dirname, join } from 'path';
import * as fs from 'fs';
import * as glob from 'fast-glob';
import * as ts from 'typescript';
import { tsquery } from '@phenomnomnominal/tsquery';

import { decode } from './parser';
import { Builder } from './generator';
import { tojava } from './java';

const PACKAGE = 'package com.squarespace.cldrengine.internal;\n\n';

const IGNORE = new Set<string>([
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

export const buildSchema = () => {
  const builder = new Builder({});
  builder.construct(ORIGIN);
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
  const builder = new Builder(types);
  builder.construct(ORIGIN);
  let code = builder.buf.join('');
  fs.writeFileSync(join(dest, 'Meta.java'), code, { encoding: 'utf-8' });

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
    fs.writeFileSync(join(dest, `${o.name}.java`), code, { encoding: 'utf-8' });
  }

  // Generate enums
  for (const o of enums) {
    if (IGNORE.has(o.name)) {
      continue;
    }
    let code = PACKAGE;
    code += tojava(o);
    fs.writeFileSync(join(dest, `${o.name}.java`), code, { encoding: 'utf-8' });
  }

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

main();
