import { join } from 'path';
import * as fs from 'fs';
import * as glob from 'fast-glob';
import * as ts from 'typescript';
import { tsquery } from '@phenomnomnominal/tsquery';

import { makedirs, write } from './utils';

import { VERSION } from '@phensley/cldr-core/lib/utils/version';

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

import * as decimalnumbers from '@phensley/cldr-core/lib/systems/numbering/autogen.names';
import * as currencies from '@phensley/cldr-core/lib/internals/numbers/autogen.currencies';

import * as plural from '@phensley/plurals/lib/autogen.rules';

import { TimeZoneStableIdIndex } from '@phensley/cldr-schema';

import { decode } from './parser';
import { Builder } from './generator';
import { tojava } from './java';
import { escapeWrap } from './utils';
import { CodeBuilder, Origin } from '@phensley/cldr-schema';
import { config as DEFAULT_CONFIG } from '@phensley/cldr/lib/config';

const INTERNAL_PACKAGE = 'package com.squarespace.cldrengine.internal;\n\n';
const API_PACKAGE = 'package com.squarespace.cldrengine.api;\n\n';

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

const COMMENTS = /\/\*.*\*\//gi;

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

export const generateSchema = () => {
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

  let internalDir = join(__dirname, '../../src/generated/java/com/squarespace/cldrengine/internal');
  makedirs(internalDir);

  let apiDir = join(__dirname, '../../src/generated/java/com/squarespace/cldrengine/api');
  makedirs(apiDir);

  // Generate the schema
  const types = interfaces.reduce((p, c) => {
    p[c.name] = c;
    return p;
  }, {});

  const origin = new CodeBuilder(DEFAULT_CONFIG).origin();

  const builder = new Builder(types);
  builder.construct(origin);
  let code = builder.buf.join('');

  const tmp = code.replace(COMMENTS, '');
  const seen = new Set<String>();
  for (const e of enums) {
    if (IGNORE.has(e.name)) {
      continue;
    }
    const re = new RegExp('\\b' + e.name);
    if (!seen.has(e.name) && re.test(tmp)) {
      code = `import com.squarespace.cldrengine.api.${e.name};\n` + code;
      seen.add(e.name);
    }
  }

  code = INTERNAL_PACKAGE + code;
  code = '/** AUTO-GENERATED DO NOT EDIT */\n\n' + code;
  write(join(internalDir, 'Meta.java'), code);

  // Generate interfaces
  for (const o of interfaces) {
    if (IGNORE.has(o.name)) {
      continue;
    }
    let code = tojava(o, types);
    if (code.indexOf('Map<') !== -1) {
      code = 'import java.util.Map;\n' + code;
    }

    const tmp = code.replace(COMMENTS, '');

    const seen = new Set<string>();
    for (const e of enums) {
      if (IGNORE.has(e.name)) {
        continue;
      }

      // Check for type after a word boundary
      const re = new RegExp('\\b' + e.name);
      if (!seen.has(e.name) && re.test(tmp)) {
        code = `import com.squarespace.cldrengine.api.${e.name};\n` + code;
        seen.add(e.name);
      }
    }
    code = INTERNAL_PACKAGE + '\n' + code;
    write(join(internalDir, `${o.name}.java`), code);
  }

  // Generate enums
  for (const o of enums) {
    if (IGNORE.has(o.name)) {
      continue;
    }
    let code = API_PACKAGE;
    code += tojava(o);
    write(join(apiDir, `${o.name}.java`), code);
  }

  writeConstants(internalDir, 'MiscData', {
    VERSION
  });

  writeConstants(internalDir, 'CalendarExternalData', {
    ...calprefs,
    ...dayperiods,
    ...timedata,
    ...weekdata
  });

  writeConstants(internalDir, 'LocaleExternalData', {
    ...languageTagAliases,
    ...localealiases,
    ...distance,
    ...locales,
    ...partition,
    ...languageTagSubtags,
    ...localeSubtags
  });

  writeConstants(internalDir, 'NumberExternalData', {
    ...decimalnumbers,
    ...currencies
  });

  writeConstants(internalDir, 'TimeZoneExternalData', {
    ...zonealiases,
    ...metazonedata,
    stableids: TimeZoneStableIdIndex.keys
  });

  writeConstants(internalDir, 'PluralExternalData', {
    ...plural
  });

  // Copy resource packs
  internalDir = join(__dirname, '../../src/generated/resources/com/squarespace/cldrengine/internal');
  makedirs(internalDir);

  const packroot = join(__dirname, '../node_modules/@phensley/cldr/packs');
  fs.readdirSync(packroot).filter(p => p.endsWith('.json')).forEach(p => {
    const src = join(packroot, p);
    const dst = join(internalDir, p);
    console.log(`copy ${p}`);
    fs.copyFileSync(src, dst);
  });

  // Copy schema config
  const configpath = join(__dirname, '../node_modules/@phensley/cldr-compiler/lib/cli/compiler/config.json');
  fs.copyFileSync(configpath, join(internalDir, 'config.json'));

  // Save timezone data (too large to embed in a Java class)
  const tzdatapath = join(internalDir, 'zonedata.json');
  fs.writeFileSync(tzdatapath, JSON.stringify(timezonedata.rawdata), { encoding: 'utf-8' });
};

const writeConstants = (dest: string, name: string, obj: any) => {
  let code = INTERNAL_PACKAGE;
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

