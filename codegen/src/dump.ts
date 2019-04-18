import { CodeBuilder, DigitsArrow, FieldArrow, Origin, Schema, ScopeArrow, Vector1Arrow, Vector2Arrow } from '@phensley/cldr-schema';
import { SchemaBuilder } from '@phensley/cldr-core/lib/internals/schema';
import { config as CONFIG } from '@phensley/cldr/lib/config';


export const buildSchema = (origin: Origin, debug: boolean = false): Schema => {
  const builder = new SchemaBuilder(debug);
  const schema = ({} as any) as Schema;
  builder.construct(schema, origin);
  return schema;
};

const dump = (o: any, depth: number = 1) => {
  const i = ' '.repeat(depth * 2);
  let s = '';
  if (o instanceof DigitsArrow) {
    s += `${i}DigitsArrow offset=${o.offset}\n`;
  } else if (o instanceof FieldArrow) {
    s += `${i}FieldArrow offset=${o.offset}\n`;
  } else if (o instanceof Vector1Arrow) {
    s += `${i}Vector1Arrow offset=${o.offset - 1}\n`;
  } else if (o instanceof Vector2Arrow) {
    s += `${i}Vector2Arrow offset=${o.offset - 1}\n`;
  } else if (o instanceof ScopeArrow) {
    s += `${i}ScopeArrow\n`;
    for (const key of Object.keys(o.map)) {
      s += `${i}${key}\n`;
      s += dump(o.map[key], depth + 1);
    }
  } else {
    for (const key of Object.keys(o)) {
      s += `${i}${key}\n`;
      s += dump(o[key], depth + 1);
    }
  }
  return s;
};

const schema = ({} as any) as Schema;
const origin = new CodeBuilder(CONFIG).origin();
const builder = new SchemaBuilder(false);
builder.construct(schema, origin);
console.log(dump(schema));
