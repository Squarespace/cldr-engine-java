import * as fs from 'fs';
import { join } from 'path';
import * as ts from 'typescript';
import { tsquery } from '@phenomnomnominal/tsquery';
import * as glob from 'fast-glob';

const SOURCE = `
type FooType = 'foo' | 'bar';
class FooClass<T> {
  private arr: T[];
}
interface FooInterface {
  bar: FooClass<FooType>;
  foo: FooClass<string>;
  quux: FooClass<number>;
}
`;

const K = ts.SyntaxKind;

/**
 * Decode the AST node as a simpler object.
 */
export const decode = (n?: ts.Node): any => {
  if (n === undefined) {
    return undefined;
  }
  switch (n.kind) {
    case K.AnyKeyword:
      return {
        kind: 'type',
        name: 'any',
        typeargs: []
      };

    case K.ArrayType:
      {
        const a = <ts.ArrayTypeNode>n;
        return {
          kind: 'arraytype',
          name: a.getText()
        };
      }

    case K.BooleanKeyword:
      return {
        kind: 'type',
        name: 'boolean',
        typeargs: []
      };

    case K.ExpressionWithTypeArguments:
      {
        const e = <ts.ExpressionStatement>n;
        return e.expression.getText();
      }

    case K.HeritageClause:
      {
        const h = <ts.HeritageClause>n;
        return decodeArray(h.types);
      }

    case K.IndexSignature:
      {
        const i = <ts.IndexSignatureDeclaration>n;
        return {
          kind: 'indexsig',
          type: decode(i.type)
          // name: i.name!.getText()
        };
      }

    case K.InterfaceDeclaration:
      {
        const i = <ts.InterfaceDeclaration>n;
        return {
          kind: 'interface',
          name: i.name.getText(),
          members: i.members.map(m => decode(m)),
          extends: decodeArray(i.heritageClauses)
        };
      }

    case K.LiteralType:
      const e = <ts.LiteralTypeNode>n;
      return {
        kind: 'literal',
        value: e.literal.getText()
      };

    case K.MethodSignature:
      {
        const m = <ts.MethodSignature>n;
        return {
          kind: 'method',
          name: m.name.getText(),
          params: decodeArray(m.parameters),
          type: decode(m.type)
        };
      }

    case K.NumberKeyword:
      return {
        kind: 'type',
        name: 'number',
        typeargs: []
      };

    case K.Parameter:
      {
        const p = <ts.ParameterDeclaration>n;
        return {
          kind: 'param',
          name: p.name.getText(),
          type: decode(p.type)
        };
      }

    case K.ParenthesizedType:
      {
        const t = <ts.ParenthesizedTypeNode>n;
        return decode(t.type);
      }

    case K.PropertySignature:
      {
        const p = <ts.PropertySignature>n;
        return {
          kind: 'property',
          name: p.name.getText(),
          type: decode(p.type!)
        };
      }

    case K.StringKeyword:
      return {
        kind: 'type',
        name: 'string',
        typeargs: []
      };

    case K.TypeAliasDeclaration:
      {
        const t = <ts.TypeAliasDeclaration>n;
        return {
          kind: 'typealias',
          name: t.name.getText(),
          type: decode(t.type)
        }
      }

    case K.TypeLiteral:
      {
        const t = <ts.TypeLiteralNode>n;
        return {
          kind: 'typeliteral',
          members: decodeArray(t.members)
        };
      }

    case K.TypeReference:
      {
        const r = <ts.TypeReferenceNode>n;
        return {
          kind: 'type',
          name: r.typeName.getText(),
          typeargs: decodeArray(r.typeArguments)
        };
      }

    case K.UnionType:
      {
        const u = <ts.UnionTypeNode>n;
        return {
          kind: 'union',
          members: decodeArray(u.types)
        }
      }

    default:
      console.log('[WARN] UNHANDLED KIND:', K[n.kind]);
      return {
        kind: 'unhandled',
        origkind: K[n.kind]
      };
  }
  return undefined;
};

/**
 * Decode an array of AST nodes.
 */
const decodeArray = (n?: ts.NodeArray<ts.Node>): any[] =>
  n === undefined ? [] : n.map(decode);

const loadSources = (pattern: string): ts.SourceFile[] =>
  glob.sync(pattern)
    .map(f => {
      const p = f.toString();
      return [p, fs.readFileSync(p, { encoding: 'utf-8' })];
    })
    .map(([p, data]) => tsquery.ast(data, p));

const safeName = (s: string) => {
  switch (s) {
    case 'short':
    case 'long':
      return `${s}_`;
  }
  return s;
};

const foo = () => {
  const ast = tsquery.ast(SOURCE);
  const result = tsquery(ast, '*', { visitAllChildren: false });
  for (const r of result) {
    const d = decode(r);
  }
};

// const main = () => {
//   const query = 'InterfaceDeclaration';
//   // const query = 'TypeAliasDeclaration';
//   const pattern = join(__dirname, '../node_modules/@phensley/cldr-schema/lib-es/**/*.d.ts');
//   const sources = loadSources(pattern);
//   const decoded: any[] = [];
//   for (const source of sources) {
//     // const ast = tsquery.ast(SOURCE);
//     // console.log(source.fileName);
//     // console.log(source.getSourceFile().text);
//     const result = tsquery(source, query, { visitAllChildren: false });
//     // console.log('--------------------');

//     for (const i of result) {
//       decoded.push(decode(i));
//     }
//   }

//   for (const d of decoded) {
//     // if (d.name === 'Digits') {
//     // console.log(JSON.stringify(d));
//     let java = tojava(d);
//     console.log(java);
//     console.log('--------');
//     // }
//   }
// };

// // main();
// // foo();
