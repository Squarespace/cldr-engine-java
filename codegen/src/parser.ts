import * as ts from 'typescript';

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
        typeargs: [],
      };

    case K.ArrayType: {
      const a = n as ts.ArrayTypeNode;
      return {
        kind: 'arraytype',
        name: a.getText(),
      };
    }

    case K.BooleanKeyword:
      return {
        kind: 'type',
        name: 'boolean',
        typeargs: [],
      };

    case K.ExpressionWithTypeArguments: {
      const e = n as ts.ExpressionStatement;
      return e.expression.getText();
    }

    case K.HeritageClause: {
      const h = n as ts.HeritageClause;
      return decodeArray(h.types);
    }

    case K.IndexSignature: {
      const i = n as ts.IndexSignatureDeclaration;
      return {
        kind: 'indexsig',
        type: decode(i.type),
        // name: i.name!.getText()
      };
    }

    case K.InterfaceDeclaration: {
      const i = n as ts.InterfaceDeclaration;
      return {
        kind: 'interface',
        name: i.name.getText(),
        members: i.members.map((m) => decode(m)),
        extends: decodeArray(i.heritageClauses),
      };
    }

    case K.LiteralType:
      const e = n as ts.LiteralTypeNode;
      return {
        kind: 'literal',
        value: e.literal.getText(),
      };

    case K.MethodSignature: {
      const m = n as ts.MethodSignature;
      return {
        kind: 'method',
        name: m.name.getText(),
        params: decodeArray(m.parameters),
        type: decode(m.type),
      };
    }

    case K.NumberKeyword:
      return {
        kind: 'type',
        name: 'number',
        typeargs: [],
      };

    case K.Parameter: {
      const p = n as ts.ParameterDeclaration;
      return {
        kind: 'param',
        name: p.name.getText(),
        type: decode(p.type),
      };
    }

    case K.ParenthesizedType: {
      const t = n as ts.ParenthesizedTypeNode;
      return decode(t.type);
    }

    case K.PropertySignature: {
      const p = n as ts.PropertySignature;
      return {
        kind: 'property',
        name: p.name.getText(),
        type: decode(p.type!),
      };
    }

    case K.StringKeyword:
      return {
        kind: 'type',
        name: 'string',
        typeargs: [],
      };

    case K.TypeAliasDeclaration: {
      const t = n as ts.TypeAliasDeclaration;
      return {
        kind: 'typealias',
        name: t.name.getText(),
        type: decode(t.type),
      };
    }

    case K.TypeLiteral: {
      const t = n as ts.TypeLiteralNode;
      return {
        kind: 'typeliteral',
        members: decodeArray(t.members),
      };
    }

    case K.TypeReference: {
      const r = n as ts.TypeReferenceNode;
      return {
        kind: 'type',
        name: r.typeName.getText(),
        typeargs: decodeArray(r.typeArguments),
      };
    }

    case K.UnionType: {
      const u = n as ts.UnionTypeNode;
      return {
        kind: 'union',
        members: decodeArray(u.types),
      };
    }

    default:
      console.log('[WARN] UNHANDLED KIND:', K[n.kind]);
      return {
        kind: 'unhandled',
        origkind: K[n.kind],
      };
  }
  return undefined;
};

/**
 * Decode an array of AST nodes.
 */
const decodeArray = (n?: ts.NodeArray<ts.Node>): any[] =>
  n === undefined ? [] : n.map(decode);
