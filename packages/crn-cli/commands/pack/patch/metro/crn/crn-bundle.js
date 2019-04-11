"use strict";
/** 
 * 修改node_modules/metro/src/shared/output/bundle.js输出产物，增加CRN相关配置文件
*/
const writeFile = require("./writeFile");

const fs = require("fs");

const path = require("path");

const chalk = require("chalk");

const moduleIdFactory = require("../../lib/createModuleIdFactory");

const PACK_CONFIG_FILE_NAME = "pack.config";


const BASE_MAPPING_FILE = function() {
  return path.resolve(process.cwd(), `bundle_output/baseMapping.json`);
};

const BU_MAPPING_FILE = function() {
  return path.resolve(process.cwd(), `bundle_output/buMapping.json`);
};

const RN_PACKAGE_JSON = function() {
  return path.resolve(process.cwd(), `node_modules/react-native/package.json`);
};

const BUNDLE_OUTPUT_PATH = function() {
  return path.resolve(process.cwd(), "bundle_output");
};

const PUBLISH_PATH = function() {
  return path.resolve(process.cwd(), "bundle_output/publish");
};

var CRNSaveBundleAndMap = function(bundle, options, log) {
  const bundleOutput = options.bundleOutput,
    encoding = options.bundleEncoding,
    sourcemapOutput = options.sourcemapOutput,
    sourcemapSourcesRoot = options.sourcemapSourcesRoot; //CRN BEGIN

  /**
   * 获得输出路径
   * 输出 react-native 版本 和 打包时间
   * 输出base mapping
   * 输出bundle, 不再输出meta
   * 输出sourcemap
   * 返回Promise.all
   */

  let code = bundle.code,
    map = bundle.map;

  if (sourcemapOutput) {
    if (sourcemapSourcesRoot !== undefined) {
      map = relativateSerializedMap(map, sourcemapSourcesRoot);
    }
  }

  var outputDir = BUNDLE_OUTPUT_PATH();

  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir);
  }

  var publishDir = PUBLISH_PATH();

  if (!fs.existsSync(publishDir)) {
    fs.mkdirSync(publishDir);
  }

  const rnJSONPath = RN_PACKAGE_JSON();

  if (fs.existsSync(rnJSONPath)) {
    var rnJSON = require(rnJSONPath);

    if (rnJSON) {
      var packConfigStr = [
        "time=" + new Date(),
        "version=" +
          (rnJSON.version || "unknown") 
      ].join("\n");
      const packConfigFile = path.join(publishDir, PACK_CONFIG_FILE_NAME);

      var wirtePackConfig = writeFile(
        packConfigFile,
        packConfigStr,
        encoding
      );
    }
  } else {
    console.log(
      chalk.yellow("没有找到node_modules/react-native下的package.json文件")
    );
  }

  if (global.CRN_BUILD_COMMON) {
    const mappingFile = BASE_MAPPING_FILE(options.platform);

    const baseMapping = moduleIdFactory.getBaseMapping();
    baseMapping.sort(function(a, b) {
      return a.id - b.id;
    });
    var writeMapping = writeFile(
      mappingFile,
      JSON.stringify(baseMapping, null, "  "),
      encoding
    );
  } else {
    const mappingFileBU = BU_MAPPING_FILE();
    const buMapping = moduleIdFactory.getBUMapping();
    buMapping.sort(function(a, b) {
      return a.id - b.id;
    });
    var writeBUMapping = writeFile(
      mappingFileBU,
      JSON.stringify(buMapping, null, "  "),
      encoding
    );
  }

  const bundleFileName = path.basename(bundleOutput);

  const bundleFile = path.join(publishDir, bundleFileName);
  var writeBundle = writeFile(bundleFile, code, encoding)
  let writeSourceMap;

  if (sourcemapOutput) {
    const sourcemapFileName = path.basename(sourcemapOutput);
    const sourcemapFile = path.resolve(outputDir, sourcemapFileName);
    writeSourceMap = writeFile(sourcemapFile, map, encoding);
  }

  var allList = [];

  if (writeMapping) {
    allList.push(writeMapping);
  }

  if (writeBUMapping) {
    allList.push(writeBUMapping);
  }

  if (writeBundle) {
    allList.push(writeBundle);
  }

  if (writeSourceMap) {
    allList.push(writeSourceMap);
  }

  if (wirtePackConfig) {
    allList.push(wirtePackConfig);
  }

  return Promise.all(allList);
};

module.exports = CRNSaveBundleAndMap;
