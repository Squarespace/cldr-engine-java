import * as fs from 'fs';
import { join } from 'path';
import { makedirs, write } from './utils';

type Type =
  | 'Boolean'
  | 'CalendarType'
  | 'ContextType'
  | 'CurrencyFormatStyleType'
  | 'CurrencySymbolWidthType'
  | 'DateFieldWidthType'
  | 'DecimalFormatStyleType'
  | 'EraWidthType'
  | 'FieldWidthType'
  | 'FormatWidthType'
  | 'RoundingModeType'
  | 'Integer'
  | 'String'
  ;

const API_PACKAGE = 'com.squarespace.cldrengine.api';
const INTERNAL_PACKAGE = 'com.squarespace.cldrengine.internal';

const API_SET = new Set<Type>([
  'CalendarType',
  'ContextType',
  'CurrencyFormatStyleType',
  'CurrencySymbolWidthType',
  'DateFieldWidthType',
  'DecimalFormatStyleType',
  'EraWidthType',
  'FieldWidthType',
  'FormatWidthType',
  'RoundingModeType',
]);

const INTERNAL_SET = new Set<Type>([
]);

interface Field {
  name: string;
  type: Type;
}

class Option {
  readonly fields: Field[] = [];

  constructor(readonly name: string, readonly extend?: string) { }

  field(name: string, type: Type): Option {
    this.fields.push({ name, type });
    return this;
  }
}

type OptionMap = { [name: string]: Option };

const CalendarFieldsOptions =
  new Option('CalendarFieldsOptions')
    .field('calendar', 'CalendarType')
    .field('width', 'FieldWidthType')
    .field('context', 'ContextType');

const EraFieldOptions =
  new Option('EraFieldsOptions')
    .field('calendar', 'CalendarType')
    .field('width', 'EraWidthType')
    .field('context', 'ContextType');

const DateFieldFormatOptions =
  new Option('DateFieldFormatOptions')
    .field('calendar', 'CalendarType')
    .field('width', 'DateFieldWidthType')
    .field('context', 'ContextType');

const DateFormatOptions =
  new Option('DateFormatOptions')
    .field('context', 'ContextType')
    .field('datetime', 'FormatWidthType')
    .field('date', 'FormatWidthType')
    .field('time', 'FormatWidthType')
    .field('wrap', 'FormatWidthType')
    .field('skeleton', 'String')
    .field('calendar', 'CalendarType')
    .field('numberSystem', 'String');

const DecimalAdjustOptions =
  new Option('DecimalAdjustOptions')
    .field('round', 'RoundingModeType')
    .field('minimumIntegerDigits', 'Integer')
    .field('maximumFractionDigits', 'Integer')
    .field('minimumFractionDigits', 'Integer')
    .field('maximumSignificantDigits', 'Integer')
    .field('minimumSignificantDigits', 'Integer');

const NumberFormatOptoions =
  new Option('NumberFormatOptions', 'DecimalAdjustOptions')
    .field('group', 'Boolean')
    .field('numberSystem', 'String');

const DecimalFormatOptions =
  new Option('DecimalFormatOptions', 'NumberFormatOptions')
    .field('style', 'DecimalFormatStyleType')
    .field('negativeZero', 'Boolean')
    .field('divisor', 'Integer')
    .field('context', 'ContextType');

const CurrencyFormatOptions =
  new Option('CurrencyFormatOptions', 'NumberFormatOptions')
    .field('divisor', 'Integer')
    .field('cash', 'Boolean')
    .field('style', 'CurrencyFormatStyleType')
    .field('symbolWidth', 'CurrencySymbolWidthType');

const INDEX = [
  CalendarFieldsOptions,
  CurrencyFormatOptions,
  DateFieldFormatOptions,
  DateFormatOptions,
  DecimalAdjustOptions,
  DecimalFormatOptions,
  EraFieldOptions,
  NumberFormatOptoions
].reduce((p, c) => {
  p[c.name] = c;
  return p;
}, {} as OptionMap);

const make = (pkg: string, opt: Option) => {
  const _imports = new Set<string>();
  const imp = imports(_imports, opt);

  let s = `package ${pkg};\n\n`;
  // s += 'import lombok.ToString;\n';
  _imports.forEach(i => s += `import ${i};\n`);
  s += '\n';
  // s += '@ToString\n';
  s += `public class ${opt.name} `;
  if (opt.extend) {
    s += `extends ${opt.extend} `;
  }
  s += '{\n\n';
  s += define(opt);
  s += '\n';
  s += constructors(opt);
  s += setters(opt.name, opt.fields);
  s += makeExtend(opt.name, opt.extend);
  s += builder(opt.name);
  s += merger(opt);
  s += '\n';
  s += tostring(opt);
  s += '\n}\n';
  return s;
};

const imports = (set: Set<String>, opt: Option) => {
  for (const field of opt.fields) {
    if (INTERNAL_SET.has(field.type)) {
      set.add(INTERNAL_PACKAGE + '.' + field.type);
      break;
    }
    if (API_SET.has(field.type)) {
      set.add(API_PACKAGE + '.' + field.type);
      break;
    }
  }
  if (opt.extend) {
    imports(set, INDEX[opt.extend]);
  }
};

const makeExtend = (cls: string, extend?: string) => {
  let s = '';
  if (!extend) {
    return s;
  }

  const parent = INDEX[extend];
  s += setters(cls, parent.fields);
  s += makeExtend(cls, parent.extend);
  return s;
};

const define = (opt: Option) => {
  let s = '';
  for (const { name, type } of opt.fields) {
    s += `  public final Option<${type}> ${name} = Option.option();\n`;
  }
  return s;
};

const constructors = (opt: Option) => {
  let s = '';
  s += `  public ${opt.name}() {\n  }\n\n`;
  s += `  public ${opt.name}(${opt.name} arg) {\n`;
  if (opt.extend) {
    s += `    super(arg);\n`;
  }
  for (const f of opt.fields) {
    s += `    this.${f.name}.set(arg.${f.name});\n`;
  }
  s += `  }\n\n`;
  return s;
};

const builder = (cls: string) => {
  let s = '';
  s += `  public static ${cls} build() {\n`;
  s += `    return new ${cls}();\n`;
  s += `  }\n\n`;
  s += `  public ${cls} copy() {\n`;
  s += `    return new ${cls}(this);\n`;
  s += `  }\n\n`;
  return s;
};

const merger = (opt: Option) => {
  let s = '';
  s += `  public ${opt.name} merge(${opt.name} ...args) {\n`;
  s += `    ${opt.name} o = new ${opt.name}(this);\n`;
  s += `    for (${opt.name} arg : args) {\n`;
  s += `      o._merge(arg);\n`;
  s += `    }\n`;
  s += `    return o;\n`;
  s += '  }\n\n';
  s += `  public void _merge(${opt.name} o) {\n`;
  if (opt.extend) {
    s += `    super._merge(o);\n`;
  }
  for (const f of opt.fields) {
    s += `    this.${f.name}.setIf(o.${f.name});\n`;
  }
  s += `  }\n\n`;
  return s;
};

const setters = (cls: string, fields: Field[]) => {
  let s = '';
  for (const { name, type } of fields) {
    s += `  public ${cls} ${name}(${type} arg) {\n`;
    s += `    this.${name}.set(arg);\n`;
    s += `    return this;\n`;
    s += `  }\n\n`;
  }
  return s;
};

const tostring = (opt: Option) => {
  let s = '';
  s += `  @Override\n`;
  s += `  public String toString() {\n`;
  s += `    StringBuilder buf = new StringBuilder("${opt.name}( ");\n`;
  s += `    this._tostring(buf);\n`;
  s += `    return buf.append(')').toString();\n`;
  s += '  }\n\n';
  s += '  protected void _tostring(StringBuilder buf) {\n';
  if (opt.extend) {
    s += `    super._tostring(buf);\n`;
  }

  for (const f of opt.fields) {
    s += `    if (${f.name}.ok()) {\n`;
    s += `      buf.append("${f.name}=").append(${f.name}).append(' ');\n`;
    s += `    }\n`;
  }
  s += '  }\n';
  return s;
};

export const generateOptions = () => {
  let apiDir = join(__dirname, '../../src/generated/java/com/squarespace/cldrengine/api');
  makedirs(apiDir);

  let code: string;

  for (const name of Object.keys(INDEX)) {
    const dest = join(apiDir, `${name}.java`);
    const type = INDEX[name];
    code = make(API_PACKAGE, type);
    console.log(`writing ${dest}`);
    write(dest, code);
  }
};

generateOptions();
