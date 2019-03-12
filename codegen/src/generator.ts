import {
  Digits,
  Field,
  Instruction,
  Origin,
  ORIGIN,
  Scope,
  ScopeMap,
  Vector1,
  Vector2,
} from '@phensley/cldr-schema';

const IMPORTS = [
  'java.util.HashMap'
];

/**
 * Generator that mirrors the dynamic schema builder in @phensley/cldr.
 * We use this to generate a Java class that provides full schema access
 * to the CLDR data.
 */

class Generator {

  private offset: number = 0;

  field(): number {
    return this.offset++;
  }

  vector1(dim: number): number {
    const off = this.offset;
    this.offset += dim;
    return off;
  }

  vector2(dim1: number, dim2: number): number {
    const off = this.offset;
    this.offset += (dim1 * dim2);
    return off;
  }
}

const RENAMES: { [x: string]: string } = {
  long: 'long_',
  short: 'short_',
  string: 'String'
};

const json = (o: any) => JSON.stringify(o);

const keyToField = (key: string) => key.toUpperCase().replace(/-/g, '_');

const fix = (f: string) => RENAMES[f] || f;

// const members = (n: any): any => {
//   let r: any = {};
//   for (const e of n.extends) {
//     r = { ...r, ...members(e) };
//   }
//   for (const m of n.members) {
//     r[m.name] = m.type
//   }
//   return r;
// };

export class Builder {

  private generator: Generator = new Generator();
  private origin!: Origin;
  buf: string[] = [];
  depth: number = 0;
  current: any;
  stack: any[] = [];
  indextypes: any = {};
  suppress = 0;

  constructor(readonly types: any) {
    this.current = this.members(types.Schema);
  }

  members(n: any): any {
    let r: any = {};
    for (const names of n.extends) {
      const type = this.types[names[0]];
      const m = this.members(type);
      r = { ...r, ...m };
    }
    for (const m of n.members) {
      r[m.name] = m.type;
    }
    return r;
  }

  append(s: string, noindent: boolean = false) {
    if (!this.suppress) {
      if (!noindent) {
        this.indent();
      }
      this.buf.push(s);
    }
  }

  indent() {
    if (!this.suppress) {
      this.buf.push('  '.repeat(this.depth));
    }
  }

  lookupType(name: string): any {
    // console.log(`lookup field ${name}`);
    const type = this.current[name];
    if (type === undefined) {
      const parents = this.stack.map(e => e.name).join(', ');
      throw new Error(`[FATAL] field "${name}"'s type is undefined: ${this.stack.length} ${parents}  keys: ${Object.keys(this.current)}`);
    }
    return type;
  }

  push(typename: string): any {
    // console.log(`push type ${typename}`);
    const type = this.types[typename];
    if (type === undefined) {
      const parents = this.stack.map(e => e.name).join(', ');
      throw new Error(`[FATAL] "${typename}" is undefined: ${this.stack.length} ${parents}`);

    }
    this.stack.push(this.current);
    this.current = this.members(type);
    return type;
  }

  pop(): any {
    this.current = this.stack.pop();
    // console.log(`pop state: ${Object.keys(this.current)}`);
    return this.current;
  }

  enter() {
    this.depth++;
  }

  exit() {
    this.depth--;
  }

  construct(inst: Instruction) {
    switch (inst.type) {
      case 'digits':
        this.constructDigits(inst);
        break;
      case 'field':
        this.constructField(inst);
        break;
      case 'origin':
        this.constructOrigin(inst);
        break;
      case 'scope':
        this.constructScope(inst);
        break;
      case 'scopemap':
        this.constructScopeMap(inst);
        break;
      case 'vector1':
        this.constructVector1(inst);
        break;
      case 'vector2':
        this.constructVector2(inst);
        break;
    }
  }

  constructDigits(inst: Digits) {
    const type = this.lookupType(inst.name);
    const typ0 = type.typeargs[0].name;
    this.indextypes[inst.dim0] = typ0;

    const dim0 = `KEY_${keyToField(inst.dim0)}`;
    const _dim0 = this.origin.getIndex(inst.dim0);
    const offset = this.generator.vector2(_dim0.size, inst.values.length * 2);
    this.append(`/* ${fix(inst.name)} = */ new DigitsArrow<${typ0}>(${offset}, ${dim0})`);
  }

  constructField(inst: Field) {
    const offset = this.generator.field();
    this.append(`/* ${fix(inst.name)} = */ new FieldArrow(${offset})`);
  }

  constructOrigin(inst: Origin) {
    this.origin = inst;

    // setup class
    this.append('package com.squarespace.cldr2.internal;\n\n');
    this.append('/** AUTO-GENERATED DO NOT EDIT */\n\n');
    for (const imp of IMPORTS) {
      this.append(`import ${imp};\n`);
    }
    this.append(`\npublic final class Meta extends MetaBase {\n`);
    this.enter();

    const header = this.buf;
    this.buf = [];

    this.append(`public static final Schema SCHEMA = new Schema(\n`);
    this.enter();
    const len = inst.block.length;
    for (let i = 0; i < len; i++) {
      if (i > 0) {
        this.append(',\n', true);
      }
      this.construct(inst.block[i]);
    }
    this.exit();
    this.append(`);\n`);
    this.exit();

    const code = this.buf;
    this.buf = [];

    // write indices as fields
    for (const key of Object.keys(this.origin.indices)) {

      const vals = this.origin.indices[key].keys;
      let type = this.indextypes[key];
      if (type === 'DateTimePatternFieldType') {
        continue;
      }
      type = type === 'string' ? 'String' : type;
      this.append(`static final KeyIndex<${type}> KEY_${keyToField(key)} = new KeyIndex<${type}>(new ${type}[] {\n`);
      this.enter();

      const len = vals.length;
      for (let i = 0; i < len; i++) {
        if (i > 0) {
          this.append(',\n');
        }
        if (type === 'String') {
          this.append(`  "${vals[i]}"`);
        } else {
          const x = vals[i];
          const num = parseInt(x, 10);
          const isstr = typeof num !== 'number' || isNaN(num);
          const k = isstr ? x.replace(/[^\w]+/g, '_').toUpperCase() : `_${x}`;
          this.append(`  ${type}.${k}`);
        }
      }
      this.exit();
      this.append('});\n\n');
    }

    for (const key of Object.keys(this.origin.values)) {
      const vals = this.origin.values[key];
      this.append(`static final String[] VAL_${keyToField(key)} = new String[] {\n`);
      this.enter();
      this.array(vals);
      this.exit();
      this.append('};\n\n');
    }

    const fields = this.buf;
    this.buf = header.concat(fields).concat(code);
    this.append('\n}\n');
  }

  constructScope(inst: Scope) {
    // console.log(`constructScope ${inst.identifier}`);
    const type = this.lookupType(inst.identifier);
    this.append(`/* ${inst.identifier} = */ new ${type.name}(\n`);
    // this.append(`public static final class ${fix(inst.identifier)} {\n`);
    this.push(type.name);
    this.enter();
    const len = inst.block.length;
    for (let i = 0; i < len; i++) {
      if (i > 0) {
        this.append(',\n', true);
      }
      this.construct(inst.block[i]);
    }
    this.append('\n');
    this.exit();
    this.pop();
    this.append(')');
  }

  constructScopeMap(inst: ScopeMap) {
    const type = this.lookupType(inst.name);
    const typ0 = type.typeargs[0].name;
    const typ1 = type.typeargs[1].name;
    const sig = type.typeargs.map((a: any) => a.name).join(', ');
    this.append(`/* Map<${sig}> ${inst.name} = */ new HashMap<String, ${typ1}>() {{\n`)
    // this.append(`public static final class ${fix(inst.name)} {\n`);
    this.enter();
    const fields = this.origin.getValues(inst.fields);
    for (const field of fields) {
      this.append(`this.put("${field}", new ${type.typeargs[1].name}(\n`);
      this.push(type.typeargs[1].name);
      // this.append(`public static final class ${fix(field)} {\n`);
      this.enter();
      const len = inst.block.length;
      for (let i = 0; i < len; i++) {
        if (i > 0) {
          this.append(',\n', true);
        }
        this.construct(inst.block[i]);
      }
      this.append('\n');
      this.exit();
      this.append('));\n');
      this.pop();
    }

    this.suppress = 1;
    for (const i of inst.block) {
      this.push(type.typeargs[1].name);
      this.construct(i);
      this.pop();
    }
    this.suppress = 0;

    this.append('\n');
    this.exit();
    this.append('}}');
  }

  constructVector1(inst: Vector1) {
    const type = this.lookupType(inst.name);
    const typ0 = type.typeargs[0].name;
    this.indextypes[inst.dim0] = typ0;

    const dim0 = `KEY_${keyToField(inst.dim0)}`;
    const _dim0 = this.origin.getIndex(inst.dim0);
    const offset = this.generator.field(); // header
    this.generator.vector1(_dim0.size);
    this.append(`/* ${fix(inst.name)} = */ new Vector1Arrow<${fix(typ0)}>(${offset}, ${dim0})`);
  }

  constructVector2(inst: Vector2) {
    const type = this.lookupType(inst.name);
    const typ0 = type.typeargs[0].name;
    const typ1 = type.typeargs[1].name;
    this.indextypes[inst.dim0] = typ0;
    this.indextypes[inst.dim1] = typ1;

    const dim0 = `KEY_${keyToField(inst.dim0)}`;
    const dim1 = `KEY_${keyToField(inst.dim1)}`;
    const _dim0 = this.origin.getIndex(inst.dim0);
    const _dim1 = this.origin.getIndex(inst.dim1);
    const offset = this.generator.field(); // header
    this.generator.vector2(_dim0.size, _dim1.size);
    this.append(`/* ${fix(inst.name)} = */ new Vector2Arrow<${fix(typ0)}, ${fix(typ1)}>(${offset}, ${dim0}, ${dim1})`);
  }

  private array(vals: string[]): void {
    const len = vals.length;
    for (let i = 0; i < len; i++) {
      const end = i < (len - 1) ? ',\n' : '\n';
      this.append(`"${vals[i]}"${end}`);
    }
  }

}
