/**
 * 添加全局lazyRequire模块
 */

'use strict';

module.exports = () => [
  //CRN BEGIN
  //增加 引入lazyRequire
  require.resolve('./Libraries/polyfills/lazyRequire.js'),
  //CRN END
  require.resolve('./Libraries/polyfills/Object.es6.js'),
  require.resolve('./Libraries/polyfills/console.js'),
  require.resolve('./Libraries/polyfills/error-guard.js'),
  require.resolve('./Libraries/polyfills/Number.es6.js'),
  require.resolve('./Libraries/polyfills/String.prototype.es6.js'),
  require.resolve('./Libraries/polyfills/Array.prototype.es6.js'),
  require.resolve('./Libraries/polyfills/Array.es6.js'),
  require.resolve('./Libraries/polyfills/Object.es7.js'),
];
