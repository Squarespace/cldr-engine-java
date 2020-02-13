import * as fs from 'fs';
import * as proc from 'child_process';
import { join } from 'path';
// import { mathSuite } from './math';
// import { currencySuite, decimalSuite } from './numbers';
// import { dateSuite } from './calendars';
// import { dateMathSuite } from './dates';
// import { relativeSuite } from './relativetime';

const root = join(__dirname, '../../../.cldrsuite');
if (!fs.existsSync(root)) {
  fs.mkdirSync(root);
}

const MODULES = ['numbers', 'math', 'mathrand', 'calendars', 'dates', 'dateconv', 'relativetime'];

for (const mod of MODULES) {
  proc.fork(join(__dirname, mod), [root]);
}
// mathSuite(root);
// currencySuite(root);
// decimalSuite(root);
// dateMathSuite(root);
// dateSuite(root);
// relativeSuite(root);
