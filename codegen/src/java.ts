const RENAMES: { [x: string]: string } = {
  long: 'long_',
  short: 'short_'
};

const fix = (f: string) => RENAMES[f] || f;

// HACKS ABOUND. Needed a quick and dirty way to migrate types
// from Typescript into Java source code.

export const tojava = (n: any, types?: any): string => {
  let r = '';
  switch (n.kind) {

    case 'arraytype':
      switch (n.name) {
        case 'number[]':
          return 'Number[]';
        case 'string[]':
          return 'String[]';
        default:
          return n.name;
      }

    case 'interface':
      r += `public class ${n.name} `;
      if (n.extends.length) {
        r += `extends ${n.extends.join(', ')} `;
      }

      const props = n.members.filter((m: any) => m.kind === 'property');
      let superprops: any[] = [];
      if (n.extends.length) {
        for (const e of n.extends) {
          const name = e[0];
          const type = types[name];
          for (const p of type.members) {
            superprops.push(p);
          }
        }
      }

      const fixup = (s: string): string => {
        // s = s.replace(/Vector1Arrow<.+>/, 'Vector1Arrow<String>');
        // s = s.replace(/Vector2Arrow<.+>/, 'Vector2Arrow<String, String>');
        s = s.replace(/Map<.+,/, 'Map<String,');
        // s = s.replace(/DigitsArrow<.+>/, 'DigitsArrow<String>');
        s = s.replace(/string/, 'String');
        return s;
      };

      r += '{\n';
      if (props.length) {
        r += '\n';
        r += props.map((m: any) =>
          `  public final ${fixup(tojava(m.type))} ${fix(m.name)};`).join('\n');
        r += '\n';
      }
      r += `\n  public ${n.name}(\n`;
      r += props.concat(superprops).map((m: any) =>
        `      ${fixup(tojava(m.type))} ${fix(m.name)}`).join(',\n');
      r += ') {\n';
      if (superprops.length) {
        r += `    super(\n`;
        r += superprops.map((m: any) =>
          `      ${fix(m.name)}`).join(',\n');
        r += '\n    );\n';
      }
      r += props.map((m: any) =>
        `    this.${fix(m.name)} = ${fix(m.name)};`).join('\n');
      r += '\n  }\n\n';
      r += '}\n';
      break;

    case 'literal':
      r += eval(n.value);
      break;

    case 'property':
      r += `public final ${tojava(n.type)} ${fix(n.name)};\n`;
      break;

    case 'type':
      switch (n.name) {
        case 'string':
          r += 'String';
          break;
        case 'ScopeArrow':
          r += 'Map';
          break;
        default:
          r += n.name;
          break;
      }
      if (n.typeargs.length) {
        r += '<';
        r += n.typeargs.map(tojava).join(', ');
        r += '>';
      }
      break;

    case 'typealias':
      r += `import java.util.Arrays;\n`;
      r += `import java.util.HashMap;\n`;
      r += `import java.util.Map;\n`;
      r += `import com.squarespace.cldrengine.internal.StringEnum;\n\n`;
      r += `public enum ${n.name} implements StringEnum<${n.name}> {\n\n`;
      r += tojava(n.type);
      r += '  ;\n\n';
      r += `  private static final Map<String, ${n.name}> REVERSE = new HashMap<>();\n`;
      r += `  static {\n`;
      r += `    Arrays.stream(${n.name}.values()).forEach(e -> REVERSE.put(e.value, e));\n`;
      r += `  }\n\n`;
      r += '  private final String value;\n\n';
      r += `  private ${n.name}(String value) {\n`;
      r += `    this.value = value;\n`;
      r += '  }\n\n';
      r += `  public String value() {\n`;
      r += `    return this.value;\n`;
      r += `  }\n\n`;
      r += `  public static ${n.name} fromString(String s) {\n`;
      r += `    return REVERSE.get(s);\n`;
      r += `  }\n`;
      r += '}\n';
      break;

    case 'union':
      {
        const len = n.members.length;
        for (let i = 0; i < len; i++) {
          if (i > 0) {
            r += ',\n';
          }
          const x = tojava(n.members[i]);
          const num = parseInt(x, 10);
          const isstr = typeof num !== 'number' || isNaN(num);
          const k = isstr ?
            x.replace(/[^\w]+/g, '_').toUpperCase()
            : `_${x}`;
          r += `  ${k}("${x}")`;
        }
        r += '\n';
        break;
      }

    default:
      console.log(`[WARN] unhandled kind ${n.kind}`);
  }
  return r;
};
