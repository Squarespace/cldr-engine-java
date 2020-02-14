import * as fs from 'fs';
import { join } from 'path';

import {
  CLDR,
  CurrencyType,
  Decimal,
  SchemaConfig,
  UnitType,
  DecimalConstants
} from '@phensley/cldr';
import { framework } from './framework';
import { LOCALES } from './data';

type Formatter = (cldr: CLDR, id: string) => string;

const currency = (cldr: CLDR, id: string) =>
  cldr.Numbers.getCurrencyDisplayName(id as CurrencyType, { context: 'begin-sentence' });

const currencyPlural = (cldr: CLDR, id: string, num: Decimal) =>
  cldr.Numbers.getCurrencyPluralName(num, id as CurrencyType, { context: 'begin-sentence' });

const language = (cldr: CLDR, id: string) =>
  cldr.General.getLanguageDisplayName(id, { context: 'begin-sentence', type: 'long' });

const script = (cldr: CLDR, id: string) =>
  cldr.General.getScriptDisplayName(id, { context: 'begin-sentence', type: 'long' });

const region = (cldr: CLDR, id: string) =>
  cldr.General.getRegionDisplayName(id, { context: 'begin-sentence', type: 'long' });

const unit = (cldr: CLDR, id: string) =>
  cldr.Units.getUnitDisplayName(id as UnitType, 'long');

const { ZERO, ONE, TWO } = DecimalConstants;

const FORMATTERS: { [key: string]: [keyof SchemaConfig, Formatter] } = {
  'currency-display': ['currency-id', currency],
  'currency-plural-0': ['currency-id', (c: CLDR, id: string) => currencyPlural(c, id, ZERO)],
  'currency-plural-1': ['currency-id', (c: CLDR, id: string) => currencyPlural(c, id, ONE)],
  'currency-plural-2': ['currency-id', (c: CLDR, id: string) => currencyPlural(c, id, TWO)],
  'language-display': ['language-id', language],
  'script-display': ['script-id', script],
  'region-display': ['region-id', region],
  'unit-display': ['unit-id', unit]
};

const buildDisplayNames = (name: string) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');

  let r = JSON.stringify({
    locales: LOCALES
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  const config = framework.config();

  for (let i = 0; i < LOCALES.length; i++) {
    const locale = LOCALES[i];
    const cldr = framework.get(locale);

    for (const name of Object.keys(FORMATTERS)) {
      const [group, func] = FORMATTERS[name];
      for (const id of config[group]!) {
        const s = func(cldr, id);
        let r = JSON.stringify({
          name, i, id, result: s
        });

        fs.writeSync(fd, r);
        fs.writeSync(fd, '\n');
      }
    }
  }

  fs.closeSync(fd);
};

export const displayNameSuite = (root: string) => {
  buildDisplayNames(join(root, 'display-names.txt'));
};

displayNameSuite(process.argv[2]);
