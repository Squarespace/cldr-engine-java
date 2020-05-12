import * as fs from 'fs';
import { join } from 'path';

import {
  buildMessageMatcher,
  parseMessagePattern,
  MessageArg,
  MessageFormatter,
  MessageFormatterOptions,
  MessageFormatFuncMap,
} from '@phensley/cldr';

import { RNG } from './rng';

const VALID: string[] = [
  '{0 upper}',
  '{1 plural =1 {1} one {# is one} many {# is many} other {# is other}}',
  '{1 plural offset:1 =1 {1} other{other}}',
  '{1 selectordinal one{#st} other{#th}}',
  '{2 select foo {FOO} bar {BAR} other {OTHER}}',
];

const GARBAGE = ["'", "''", '-', '{-', ',', ' ', '{', '}', '*'];

const buildMessage = (name: string) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');

  const rng = new RNG('cldr-engine');

  const generate = (i: number, threshold: number) => {
    const base = VALID[i % VALID.length];
    let s = '';
    for (let i = 0; i < base.length; i++) {
      if (rng.rand() < threshold) {
        s += GARBAGE[i % GARBAGE.length];
      } else {
        s += base[i];
      }
    }
    return s;
  };

  const formatters: MessageFormatFuncMap = {
    upper: (args: MessageArg[], _options: string[]) =>
      args[0] ? args[0].toUpperCase() : '',
  };

  const options: MessageFormatterOptions = {
    language: 'en',
    region: 'US',
    formatters,
  };
  const formatter = new MessageFormatter(options);
  const matcher = buildMessageMatcher(Object.keys(formatters));

  const STRS = ['foo', 'bar'];
  const NUMS = [0, 1.0, 1, 5];
  const SELECT = ['foo', 'bar', 'other'];

  for (let i = 0; i < 300; i++) {
    const args = [
      STRS[i % STRS.length],
      NUMS[i % NUMS.length],
      SELECT[i % SELECT.length],
    ];
    const m = VALID[i % VALID.length];
    const s = formatter.format(m, args, {});
    let r = {
      type: 'fixed',
      args,
      message: m,
      result: s,
    };
    fs.writeSync(fd, JSON.stringify(r));
    fs.writeSync(fd, '\n');
  }

  for (let i = 0; i < 500000; i++) {
    for (const threshold of [0.3, 0.1]) {
      const m = generate(i, threshold);
      const c = parseMessagePattern(m, matcher);
      fs.writeSync(
        fd,
        JSON.stringify({
          type: 'random',
          message: m,
          code: c,
        }),
      );
      fs.writeSync(fd, '\n');
    }
  }

  fs.closeSync(fd);
};

const messageSuite = (root: string) => {
  buildMessage(join(root, 'messages.txt'));
};

messageSuite(process.argv[2]);
