import { dirname, join } from 'path';
import * as fs from 'fs';
import * as glob from 'fast-glob';
import * as ts from 'typescript';
import { tsquery } from '@phenomnomnominal/tsquery';

import * as calprefs from '@phensley/cldr-core/lib/internals/calendars/autogen.calprefs';
import * as dayperiods from '@phensley/cldr-core/lib/internals/calendars/autogen.dayperiods';
import * as timedata from '@phensley/cldr-core/lib/internals/calendars/autogen.timedata';
import * as weekdata from '@phensley/cldr-core/lib/internals/calendars/autogen.weekdata';

import * as distance from '@phensley/locale-matcher/lib/autogen.distance';
import * as languageTagAliases from '@phensley/language-tag/lib/autogen.aliases';
import * as languageTagSubtags from '@phensley/language-tag/lib/autogen.subtags';
import * as localealiases from '@phensley/locale/lib/autogen.aliases';
import * as locales from '@phensley/cldr-core/lib/locale/autogen.locales';
import * as localeSubtags from '@phensley/locale/lib/autogen.subtags';
import * as metazonedata from '@phensley/cldr-core/lib/systems/calendars/autogen.zonedata';
import * as partition from '@phensley/locale-matcher/lib/autogen.partition';
import * as timezonedata from '@phensley/timezone/lib/autogen.zonedata';
import * as zonealiases from '@phensley/cldr-core/lib/systems/calendars/autogen.aliases';

import { TimeZoneStableIdIndex } from '@phensley/cldr-schema';

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
  'DigitsArrow',
  'Field',
  'FieldArrow',
  'Instruction',
  'KeyIndex',
  'PrimitiveBundle',
  'Origin',
  'Scope',
  'ScopeArrow',
  'ScopeMap',
  'Vector1',
  'Vector1Arrow',
  'Vector2',
  'Vector2Arrow'
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
  const root = join(__dirname, '../node_modules/@phensley/cldr-types/lib/**/*.d.ts');
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

  writeConstants(dest, 'CalendarExternalData', {
    ...calprefs,
    ...dayperiods,
    ...timedata,
    ...weekdata
  });

  // Generate constants
  writeConstants(dest, 'LocaleExternalData', {
    ...languageTagAliases,
    ...localealiases,
    ...distance,
    ...locales,
    ...partition,
    ...languageTagSubtags,
    ...localeSubtags
  });

  writeConstants(dest, 'TimeZoneExternalData', {
    ...zonealiases,
    ...metazonedata,
    stableids: TimeZoneStableIdIndex.keys
  });

  // Copy resource packs
  dest = join(__dirname, '../../src/generated/resources/com/squarespace/cldrengine/internal');
  makedirs(dest);

  const packroot = join(__dirname, '../node_modules/@phensley/cldr/packs');
  fs.readdirSync(packroot).filter(p => p.endsWith('.json')).forEach(p => {
    const src = join(packroot, p);
    const dst = join(dest, p);
    console.log(`copy ${p}`);
    fs.copyFileSync(src, dst);
  });

  // Copy schema config
  const configpath = join(__dirname, '../node_modules/@phensley/cldr-compiler/lib/cli/compiler/config.json');
  fs.copyFileSync(configpath, join(dest, 'config.json'));

  // Save timezone data (too large to embed in a Java class)
  const tzdatapath = join(dest, 'zonedata.json');
  fs.writeFileSync(tzdatapath, JSON.stringify(timezonedata.rawdata), { encoding: 'utf-8' });
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
