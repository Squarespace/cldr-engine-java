type Type =
  | 'ContextType'
  | 'RoundingModeType'
  | 'Integer'
  ;

const INTERNAL = 'com.squarespace.cldrengine.internal';

const IMPORT: { [pkg: string]: string } = {
  'ContextType': INTERNAL,
  'RoundingModeType': INTERNAL
};

interface Field {
  name: string;
  type: Type;
}

// interface Option {
//   name: string;
//   fields: Field[];
// }

class Option {
  readonly fields: Field[] = [];

  constructor(readonly name: string, readonly extend?: string) { }

  field(name: string, type: Type): Option {
    this.fields.push({ name, type });
    return this;
  }

}

type OptionMap = { [name: string]: Option };

const DecimalAdjustOptions =
  new Option('DecimalAdjustOptions')
    .field('round', 'RoundingModeType')
    .field('minimumIntegerDigits', 'Integer')
    .field('maximumFractionDigits', 'Integer')
    .field('minimumFractionDigits', 'Integer')
    .field('maximumSignificantDigits', 'Integer')
    .field('minimumSignificantDigits', 'Integer');

const NumberFormatOptions =
  new Option('NumberFormatOptions', 'DecimalAdjustOptions')
    .field('context', 'ContextType');

const INDEX = [
  DecimalAdjustOptions,
  NumberFormatOptions
].reduce((p, c) => {
  p[c.name] = c;
  return p;
}, {} as OptionMap);

const make = (pkg: string, opt: Option) => {
  const _imports = new Set<string>();
  const imp = imports(_imports, opt);

  let s = `package ${pkg};\n\n`;
  _imports.forEach(i => s += `import ${i};\n`);
  s += '\n';
  s += `public class ${opt.name} `;
  if (opt.extend) {
    s += `extends ${opt.extend} `;
  }
  s += '{\n\n';
  s += define(opt.name, opt.fields);
  s += '\n';
  s += setters(opt.name, opt.fields);
  s += makeExtend(opt.name, opt.extend);
  s += '\n}\n';
  console.log(s);
};

const imports = (set: Set<String>, opt: Option) => {
  for (const field of opt.fields) {
    if (IMPORT[field.type]) {
      set.add(IMPORT[field.type] + '.' + field.type);
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

const define = (cls: string, fields: Field[]) => {
  let s = '';
  for (const { name, type } of fields) {
    s += `  public final Option<${type}> ${name} = Option.option();\n`;
  }
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

export const generateOptions = () => {
  const pkg = 'com.squarespace.cldrengine.options';
  make(pkg, NumberFormatOptions);
};

generateOptions();
