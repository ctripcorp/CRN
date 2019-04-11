#!/usr/bin/env node
"use strict";
const Promise = require("promise");
const packageJson = require("./package.json");
const printHelp = require("./tools/printHelp");
const printVersion = require("./tools/printVersion");
const logOutput = require("./tools/logOutput");
const fs = require("fs");

process.on("uncaughtException", err => {
  if (err) {
    logOutput.error(err.stack || err.message);
  }
  process.exit(1);
});

let args = process.argv.slice(2);
if (args.length === 0 || args[0] == '--help' || args[0] == '-h' || args[0] == '-H') {
  return printHelp("cli", packageJson);
}
if (args[0] == '--version' || args[0] == '-v' || args[0] == '-V') {
  return printVersion();
}

let commandName = args[0];
let command = !!commandName ? getDocumentCmds(commandName) : false;
if (!commandName || !command) {
  logOutput.error(`The command '${commandName}' unrecognized!`);
  printHelp("cli", packageJson);
  process.exit(1);
}

let result = command[0](args);
result instanceof Promise ? result.catch(err => {
  logOutput.error(`The command '${args[0]}' completed fail: ${err.stack || err.message || "no error info"}`)
}) : false;


function getDocumentCmds(commandName) {
  let documentCmds = {
    "init": [require("./commands/init/init"), "init project"],
    "start": [require("./commands/server/start"), "start server"],
    "run-ios": [require("./commands/runIOS/runIOS"), "start ios"],
    "run-android": [require("./commands/runAndroid/runAndroid"), "strart android"],
    'pack': [require("./commands/pack/localPack"), "pack"],
    "--help": [printHelp, "show help"],
    "-h": [printHelp, "show help"],
    "--version": [printVersion, "show version"],
    "-v": [printVersion, "show version"]
  };
  return documentCmds[commandName];
}

