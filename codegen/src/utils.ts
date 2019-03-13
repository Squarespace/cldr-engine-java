
export const lineWrap = (max: number, sep: string, values: string[]): string => {
  let res = '';
  let width = 0;
  max -= 2;
  for (let i = 0; i < values.length; i++) {
    const value = values[i];
    if (width + value.length + 2 > max) {
      res += '\n';
      width = 0;
    }
    if (i > 0) {
      res += `${sep} `;
    } else {
      res += '  ';
    }
    width += 2;
    res += value;
    width += value.length;
  }
  return res;
};

export const escapeWrap = (s: string, width: number): string[] => {
  const lines: string[] = [];
  let r = '';
  const len = s.length;
  for (let i = 0; i < len; i++) {
    const c = s[i];
    switch (c) {
      case '"':
        r += '\\"';
        break;
      case '\t':
        r += '\\t';
        break;
      case '\r':
        r += '\\r';
        break;
      case '\n':
        r += '\\n';
        break;
      default:
        r += c;
        break;
    }
    if (r.length >= width) {
      lines.push(r);
      r = '';
    }
  }
  if (r.length) {
    lines.push(r);
  }
  return lines.map(s => `"${s}"`);
}