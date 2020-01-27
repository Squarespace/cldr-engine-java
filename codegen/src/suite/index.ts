import * as fs from 'fs';
import { join } from 'path';
import { currencySuite, decimalSuite } from './numbers';

const root = join(__dirname, '../../../.cldrsuite');
if (!fs.existsSync(root)) {
  fs.mkdirSync(root);
}
currencySuite(root);
decimalSuite(root);
