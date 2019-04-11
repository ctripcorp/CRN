/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 *
 * @format
 */
"use strict";

const MAGIC_RAM_BUNDLE_NUMBER = require("./magic-number");

const buildSourcemapWithMetadata = require("./buildSourcemapWithMetadata");

const mkdirp = require("mkdirp");

const path = require("path");

const relativizeSourceMapInline = require("../../../lib/relativizeSourceMap");

const writeFile = require("../writeFile");

const writeSourceMap = require("./write-sourcemap");

const _require = require("./util"),
  joinModules = _require.joinModules;

const MAGIC_RAM_BUNDLE_FILENAME = "UNBUNDLE";
const MODULES_DIR = "js-modules";
//CRN BEGIN
const CRNSaveAsAssets = require("./crn-as-assets"); 
//CRN END

/**
 * Saves all JS modules of an app as single files
 * The startup code (prelude, polyfills etc.) are written to the file
 * designated by the `bundleOuput` option.
 * All other modules go into a 'js-modules' folder that in the same parent
 * directory as the startup file.
 */

function saveAsAssets(bundle, options, log) {
  const bundleOutput = options.bundleOutput,
    encoding = options.bundleEncoding,
    sourcemapOutput = options.sourcemapOutput,
    sourcemapSourcesRoot = options.sourcemapSourcesRoot;
    //CRN BEGIN
    return CRNSaveAsAssets(bundle, options, log, writeModules); 
    //CRN END
}

function createDir(dirName) {
  return new Promise((resolve, reject) =>
    mkdirp(dirName, error => (error ? reject(error) : resolve()))
  );
}

function writeModuleFile(module, modulesDir, encoding) {
  const code = module.code,
    id = module.id; 
  //CRN BEGIN 输出剔除common包中的模块
  if (id < 666666) {
    return Promise.resolve();
  } //CRN END

  return writeFile(path.join(modulesDir, id + ".js"), code, encoding);
}

function writeModules(modules, modulesDir, encoding) {
  const writeFiles = modules.map(module =>
    writeModuleFile(module, modulesDir, encoding)
  );
  return Promise.all(writeFiles);
}

function writeMagicFlagFile(outputDir) {
  const buffer = Buffer.alloc(4);
  buffer.writeUInt32LE(MAGIC_RAM_BUNDLE_NUMBER, 0);
  return writeFile(path.join(outputDir, MAGIC_RAM_BUNDLE_FILENAME), buffer);
}

module.exports = saveAsAssets;
