import * as fs from 'fs';
import * as zlib from 'zlib';
import { join } from 'path';
import { CodeBuilder, DigitsArrow, FieldArrow, Origin, Schema, ScopeArrow, Vector1Arrow, Vector2Arrow } from '@phensley/cldr-schema';
import { VERSION } from '@phensley/cldr-core/lib/utils/version';
import { SchemaBuilder } from '@phensley/cldr-core/lib/internals/schema';
import { checksumIndices } from '@phensley/cldr-core/lib/resource/checksum';
import { config as CONFIG } from '@phensley/cldr/lib/config';

export const loader = (lang: string) => {
  const path = join(__dirname, `../node_modules/@phensley/cldr/packs/${lang}.json.gz`);
    const compressed = fs.readFileSync(path);
    return zlib.gunzipSync(compressed).toString('utf-8');
};

export const buildSchema = (origin: Origin, debug: boolean = false): Schema => {
  const builder = new SchemaBuilder(debug);
  const schema = ({} as any) as Schema;
  builder.construct(schema, origin);
  return schema;
};

type Entry = [number, string, number, number] | [number, string];

/**
 * Scan the schema and collect the scope names and start/end offsets.
 */
const scan = (o: any, depth: number = 1): Entry[] => {
  let e: Entry[] = [];
  if (o instanceof DigitsArrow) {
    const start = o.offset;
    e.push([depth, 'DigitsArrow', start, start + o.index.size * o.size2]);
  } else if (o instanceof FieldArrow) {
    e.push([depth, 'FieldArrow', o.offset, o.offset + 1]);
  } else if (o instanceof Vector1Arrow) {
    const start = o.offset - 1;
    e.push([depth, 'Vector1Arrow', start, start + o.len + 1]);
  } else if (o instanceof Vector2Arrow) {
    const start = o.offset - 1;
    e.push([depth, 'Vector2Arrow', start, start + o.size + 1]);
  } else if (o instanceof ScopeArrow) {
    e.push([depth, 'ScopeArrow']);
    for (const key of Object.keys(o.map)) {
      e.push([depth, key]);
      e = e.concat(scan(o.map[key], depth + 1));
    }
  } else {
    for (const key of Object.keys(o)) {
      e.push([depth, key]);
      e = e.concat(scan(o[key], depth + 1));
    }
  }
  return e;
};

/**
 * Display the schema entries.
 */
const display = (entries: Entry[], strings: string[]) => {
  let next = 0;
  for (let i = 0; i < entries.length; i++) {

  }
  for (const e of entries) {
    if (e.length === 2) {
      const [depth, key] = e;
      console.log(`${' '.repeat(depth)}${key}`);
    } else {
      const [depth, key, start, end] = e;
      console.log(`${' '.repeat(depth)}${key} (${start}, ${end})`);
      console.log(' '.repeat(depth), JSON.stringify(strings.slice(start, end)));
    }
  }
};

const schema = ({} as any) as Schema;
const origin = new CodeBuilder(CONFIG).origin();
const ck = checksumIndices(VERSION, origin.indices);
console.log(`Checksum ${ck}`);

const builder = new SchemaBuilder(false);
builder.construct(schema, origin);

const data = JSON.parse(loader('en'));
const strings = data.scripts.Latn.strings.split('\t');
const entries = scan(schema);
display(entries, strings);
