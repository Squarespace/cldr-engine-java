import * as fs from 'fs';
import { join } from 'path';

import {
  ACCELERATION,
  ANGLE,
  AREA,
  CONSUMPTION,
  DIGITAL,
  DIGITAL_DECIMAL,
  DURATION,
  ELECTRIC,
  ENERGY,
  FORCE,
  FREQUENCY,
  GRAPHICS_PER,
  GRAPHICS_PIXEL,
  LENGTH,
  MASS,
  POWER,
  PRESSURE,
  SPEED,
  TORQUE,
  VOLUME,
  VOLUME_UK,
  FactorDef,
  UnitFactors,
} from '@phensley/unit-converter';
import { timed } from "../utils";

const FACTORS: { [name: string]: FactorDef[] } = {
  ACCELERATION,
  ANGLE,
  AREA,
  CONSUMPTION,
  DIGITAL,
  DIGITAL_DECIMAL,
  DURATION,
  ELECTRIC,
  ENERGY,
  FORCE,
  FREQUENCY,
  GRAPHICS_PER,
  GRAPHICS_PIXEL,
  LENGTH,
  MASS,
  POWER,
  PRESSURE,
  SPEED,
  TORQUE,
  VOLUME,
  VOLUME_UK,
};

const buildUnitConvert = (name: string) => {
  const fd = fs.openSync(name, 'w');

  for (const name of Object.keys(FACTORS)) {
    const factors = FACTORS[name];
    const set = new UnitFactors(factors);
    for (const src of set.units) {
      for (const dst of set.units) {
        const conv = set.get(src, dst)!;
        fs.writeSync(
          fd,
          JSON.stringify({
            name,
            src,
            dst,
            path: conv.path,
            factors: conv.factors.map((x) => x.toString()),
          }),
        );
        fs.writeSync(fd, '\n');
      }
    }
  }

  fs.closeSync(fd);
};

const unitConvertSuite = (root: string) => {
  let name = 'unit-convert.txt';
  timed(name, () => buildUnitConvert(join(root, name)));
};

unitConvertSuite(process.argv[2]);
