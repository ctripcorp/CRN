"use strict";
/** 
 * 修改node_modules/metro/src/shared/output/RamBundle/as-assets.js输出产物，增加CRN相关配置文件
*/
const path = require("path");

const _require = require("./util"),
  joinModules = _require.joinModules;

const writeFile = require("../writeFile");

const MAGIC_RAM_BUNDLE_NUMBER = require("./magic-number");

const buildSourcemapWithMetadata = require("./buildSourcemapWithMetadata");

const writeSourceMap = require("./write-sourcemap"); // 增加path、fs、config依赖

const fs = require("fs");

const chalk = require("chalk");

const moduleIdFactory = require("../../../lib/createModuleIdFactory");

var mappingList = [],
  combineOutputV2 = []; //base.mapping输出和config输出

const MAGIC_RAM_BUNDLE_FILENAME = "_crn_unbundle";
const MODULES_DIR = "js-modules";
const CONFIG_FILE_NAME_V2 = "_crn_config_v2";
const PACK_CONFIG_FILE_NAME = "pack.config";
const BUILD_ENV_JSON = "__build_env__.json";

const RN_PACKAGE_JSON = function () {
  return path.resolve(process.cwd(), `node_modules/react-native/package.json`);
};

const BU_MAPPING_FILE = function () {
  return path.resolve(process.cwd(), `bundle_output/buMapping.json`);
};


const BUNDLE_OUTPUT_PATH = function () {
  return path.resolve(process.cwd(), "bundle_output");
};

const PUBLISH_PATH = function () {
  return path.resolve(process.cwd(), "bundle_output/publish");
};
/**
 * Saves all JS modules of an app as single files
 * The startup code (prelude, polyfills etc.) are written to the file
 * designated by the `bundleOuput` option.
 * All other modules go into a 'js-modules' folder that in the same parent
 * directory as the startup file.
 */

function CRNSaveAsAssets(bundle, options, log, writeModules) {
  const bundleOutput = options.bundleOutput,
    encoding = options.bundleEncoding,
    sourcemapOutput = options.sourcemapOutput,
    sourcemapSourcesRoot = options.sourcemapSourcesRoot;
  var outputDir = BUNDLE_OUTPUT_PATH();

  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir);
  }

  var publishDir = PUBLISH_PATH();

  if (!fs.existsSync(publishDir)) {
    fs.mkdirSync(publishDir);
  } //获取base.mapping

  mappingList = moduleIdFactory.getBaseMapping();

  const startupModules = bundle.startupModules,
    lazyModules = bundle.lazyModules;

  const startupCode = joinModules(startupModules);

  const modulesDir = path.join(publishDir, MODULES_DIR);

  if (!fs.existsSync(modulesDir)) {
    fs.mkdirSync(modulesDir);
  }

  const configFileV2 = path.join(publishDir, CONFIG_FILE_NAME_V2);

  combineOutputV2.push("main_module=666666");
  combineOutputV2.push("module_path=js-modules");
  const writeConfigV2 = writeFile(
    configFileV2,
    combineOutputV2.join("\n"),
    encoding
  ); //输出magicFlagFile

  const magicFile = path.join(publishDir, MAGIC_RAM_BUNDLE_FILENAME);

  /* global Buffer: true */

  const buffer = Buffer(4);
  buffer.writeUInt32LE(MAGIC_RAM_BUNDLE_NUMBER);
  const writeUNBUNDLE = writeFile(magicFile, buffer); //输出 react-native 版本 和 打包时间

  const rnJSONPath = RN_PACKAGE_JSON();

  if (fs.existsSync(rnJSONPath)) {
    var rnJSON = require(rnJSONPath);

    if (rnJSON) {
      var buildEnv = {
        react_native_version: rnJSON.version
      };
      const buildEnvFile = path.join(outputDir, BUILD_ENV_JSON);

      var wirteBuildEnvConfig = writeFile(
        buildEnvFile,
        JSON.stringify(buildEnv),
        encoding
      );
    }


    if (rnJSON) {
      var packConfigStr = [
        "time=" + new Date(),
        "version=" +
        (rnJSON.version || "unknown")
      ].join("\n");
      const packConfigFile = path.join(publishDir, PACK_CONFIG_FILE_NAME);

      var wirtePackConfig = writeFile(packConfigFile, packConfigStr, encoding);
    }
  } else {
    console.log(
      chalk.yellow("没有找到node_modules/react-native下的package.json文件")
    );
  }

  if (!options.buildCommon) {
    const mappingFileBU = BU_MAPPING_FILE();

    const buMapping = moduleIdFactory.getBUMapping();

    buMapping.sort(function (a, b) {
      return a.id - b.id;
    });
    var writeBUMapping = writeFile(
      mappingFileBU,
      JSON.stringify(buMapping, null, "  "),
      encoding
    );
  } //输出sourcemap

  let wroteSourceMap;

  if (sourcemapOutput) {
    const sourcemapFileName = path.basename(sourcemapOutput);
    const sourcemapFile = path.join(outputDir, sourcemapFileName);

    const sourceMap = buildSourcemapWithMetadata({
      fixWrapperOffset: true,
      lazyModules: lazyModules.concat(),
      moduleGroups: null,
      startupModules: startupModules.concat()
    });

    if (sourcemapSourcesRoot !== undefined) {
      relativizeSourceMapInline(sourceMap, sourcemapSourcesRoot);
    }

    wroteSourceMap = writeSourceMap(
      sourcemapOutput,
      JSON.stringify(sourceMap),
      log
    );
  } //返回promise all

  const writeJSModules = writeModules(lazyModules, modulesDir, encoding);
  var allList = [];

  if (writeBUMapping) {
    allList.push(writeBUMapping);
  }

  if (writeJSModules) {
    allList.push(writeJSModules);
  }

  if (writeConfigV2) {
    allList.push(writeConfigV2);
  }

  if (writeUNBUNDLE) {
    allList.push(writeUNBUNDLE);
  }

  if (writeSourceMap) {
    allList.push(writeSourceMap);
  }

  if (wirtePackConfig) {
    allList.push(wirtePackConfig);
  }

  if (wirteBuildEnvConfig) {
    allList.push(wirteBuildEnvConfig);
  }

  return Promise.all(allList);
}

module.exports = CRNSaveAsAssets;
