import * as fs from 'fs';
import { join } from 'path';
import { mathSuite } from './math';
import { currencySuite, decimalSuite } from './numbers';
import { dateSuite } from './calendars';
import { relativeSuite } from './relativetime';

const root = join(__dirname, '../../../.cldrsuite');
if (!fs.existsSync(root)) {
  fs.mkdirSync(root);
}

mathSuite(root);
currencySuite(root);
decimalSuite(root);
dateSuite(root);
relativeSuite(root);