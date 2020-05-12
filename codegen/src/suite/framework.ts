import * as fs from 'fs';
import { join } from 'path';
import { inspect } from 'util';

import { CLDRFramework } from '@phensley/cldr';

export const dump = (o: any) => inspect(o, true, undefined, true);

export const loader = (lang: string) => {
  const path = join(
    __dirname,
    `../../node_modules/@phensley/cldr/packs/${lang}.json`,
  );
  return fs.readFileSync(path).toString('utf-8');
};

export const framework = new CLDRFramework({
  loader,
});
