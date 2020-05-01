const readline = require("readline");
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
  terminal: false
});

const COLORS = {
  Reset: "\x1b[0m",
  Bright: "\x1b[1m",
  Dim: "\x1b[2m",
  Underscore: "\x1b[4m",
  Blink: "\x1b[5m",
  Reverse: "\x1b[7m",
  Hidden: "\x1b[8m",

  FgBlack: "\x1b[30m",
  FgRed: "\x1b[31m",
  FgGreen: "\x1b[32m",
  FgYellow: "\x1b[33m",
  FgBlue: "\x1b[34m",
  FgMagenta: "\x1b[35m",
  FgCyan: "\x1b[36m",
  FgWhite: "\x1b[37m",

  BgBlack: "\x1b[40m",
  BgRed: "\x1b[41m",
  BgGreen: "\x1b[42m",
  BgYellow: "\x1b[43m",
  BgBlue: "\x1b[44m",
  BgMagenta: "\x1b[45m",
  BgCyan: "\x1b[46m",
  BgWhite: "\x1b[47m"
};

const colorize = color => message => `${color}${message}${COLORS.Reset}`;

const colorizeLevel = level => {
  switch (level) {
    case "DEBUG":
      return colorize(COLORS.Dim);
    case "INFO":
      return colorize(COLORS.FgGreen);
    case "WARN":
      return colorize(COLORS.BgYellow + COLORS.FgBlack);
    case "ERROR":
      return colorize(COLORS.BgRed + COLORS.FgBlack);
    default:
      return colorize(COLORS.BgCyan + COLORS.FgBlack);
  }
};

const INGORED_FIELDS = [
  'severity',
  '@timestamp',
  'hostname',
]

rl.on("line", function (line) {
  try {
    const jsonlog = JSON.parse(line);
    const { level, requestId, message, stacktrace, ...rest } = jsonlog;
    if (requestId) {
      rest.requestId = requestId;
    }
    console.log(colorizeLevel(level)(message));
    Object.entries(rest).filter(([k, _]) => !INGORED_FIELDS.includes(k)).forEach(([k, v]) => console.log(`\t${k}=${v}`));
    if (stacktrace) {
      stacktrace
        .split('\n\t')
        .slice(0, 20)
        .forEach(line =>
          console.log(`\t${colorize(COLORS.FgRed)(line)}`)
        );
    }
  } catch (e) {
    console.log(line);
  }
});
