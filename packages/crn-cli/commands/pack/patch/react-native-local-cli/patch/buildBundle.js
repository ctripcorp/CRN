"use strict";
/** 
 * 新增build-common参数传递
*/
Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

function _Server() {
  const data = _interopRequireDefault(require("metro/src/Server"));

  _Server = function () {
    return data;
  };

  return data;
}

function _bundle() {
  const data = _interopRequireDefault(require("metro/src/shared/output/bundle"));

  _bundle = function () {
    return data;
  };

  return data;
}

function _path() {
  const data = _interopRequireDefault(require("path"));

  _path = function () {
    return data;
  };

  return data;
}

var _saveAssets = _interopRequireDefault(require("./saveAssets"));

var _loadMetroConfig = _interopRequireDefault(require("../../tools/loadMetroConfig"));

var _logger = _interopRequireDefault(require("../../tools/logger"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; var ownKeys = Object.keys(source); if (typeof Object.getOwnPropertySymbols === 'function') { ownKeys = ownKeys.concat(Object.getOwnPropertySymbols(source).filter(function (sym) { return Object.getOwnPropertyDescriptor(source, sym).enumerable; })); } ownKeys.forEach(function (key) { _defineProperty(target, key, source[key]); }); } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

async function buildBundle(args, ctx, output = _bundle().default) {
  const config = await (0, _loadMetroConfig.default)(ctx, {
    resetCache: args.resetCache,
    config: args.config,
    //增加 传递build-common参数
    buildCommon: args.buildCommon
    //CRN END
  }); // This is used by a bazillion of npm modules we don't control so we don't
  // have other choice than defining it as an env variable here.

  process.env.NODE_ENV = args.dev ? 'development' : 'production';
  let sourceMapUrl = args.sourcemapOutput;

  if (sourceMapUrl && !args.sourcemapUseAbsolutePath) {
    sourceMapUrl = _path().default.basename(sourceMapUrl);
  }

  const requestOpts = {
    entryFile: args.entryFile,
    sourceMapUrl,
    dev: args.dev,
    minify: args.minify !== undefined ? args.minify : !args.dev,
    platform: args.platform,
    //CRN BEGIN
    //增加 传递build-common参数
    buildCommon: args.buildCommon,
    //CRN END
  };
  const server = new (_Server().default)(config);

  try {
    const bundle = await output.build(server, requestOpts);
    await output.save(bundle, args, _logger.default.info); // Save the assets of the bundle

    const outputAssets = await server.getAssets(_objectSpread({}, _Server().default.DEFAULT_BUNDLE_OPTIONS, requestOpts, {
      bundleType: 'todo'
    })); // When we're done saving bundle output and the assets, we're done.

    return await (0, _saveAssets.default)(outputAssets, args.platform, args.assetsDest);
  }
  //CRN BEGIN
  //增加 添加catch异常抛出，打包报错，无法继续执行
  catch(error){
    console.error('捕捉到报错异常, 无法继续执行, 打包失败!');
    if (error) {
        var message = error.stack ? error.stack : error.message;
        console.error("error: " + message);
        throw error;
    }
    process.exit(-1);
  }
  //CRN END 
  finally {
    server.end();
  }
}

var _default = buildBundle;
exports.default = _default;