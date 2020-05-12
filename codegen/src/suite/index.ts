import * as fs from 'fs';
import * as proc from 'child_process';
import { join } from 'path';

const root = join(__dirname, '../../../.cldrsuite');
if (!fs.existsSync(root)) {
  fs.mkdirSync(root);
}

const MODULES = [
  'calendars',
  'dates',
  'dateconv',
  'math',
  'mathrand',
  'message',
  'names',
  'numbers',
  'relativetime',
  'unitconv',
];

for (const mod of MODULES) {
  proc.fork(join(__dirname, mod), [root]);
}
