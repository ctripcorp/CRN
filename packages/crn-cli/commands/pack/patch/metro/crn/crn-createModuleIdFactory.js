"use strict";
/** 
 * 修改模块ID生成规则，react-native模块从0开始，业务模块从666666开始
*/
var fs = require("fs");
var path = require("path");

var mappingContainer = [];
var mappingContainerBU = [];
var nextModuleId = {
  rn: -1,
  business: 666665
};

/**
 * 生成业务包模块ID和COMMON包模块ID
 */
function generateId() {
  if (!global.CRN_BUILD_COMMON) {
    return ++nextModuleId.business;
  }
  return ++nextModuleId.rn;
}

function createModuleIdFactory() {
  if(mappingContainer.length == 0){
    var baseMappingPath = path.join(process.cwd(), "bundle_output","baseMapping.json")
    if(fs.existsSync(baseMappingPath)){
      mappingContainer = require(baseMappingPath);
      nextModuleId.rn = mappingContainer[mappingContainer.length-1].id;
    }
  }
  if(mappingContainerBU.length == 0 && !global.CRN_BUILD_COMMON){
    var buMappingPath = path.join(process.cwd(), "bundle_output","buMapping.json")
    if(fs.existsSync(buMappingPath)){
      mappingContainerBU = require(buMappingPath);
      nextModuleId.business = mappingContainerBU[mappingContainerBU.length-1].id;
    }
  }

  return path => { 
    var oldModule = [];
    path = path.replace(process.cwd(), "");
    if (mappingContainer && mappingContainer.length > 0) {
      oldModule = mappingContainer.filter(element => {
        return path === element.path;
      });
    }

    var oldBUModule = [];
    if (mappingContainerBU && mappingContainerBU.length > 0) {
      oldBUModule = mappingContainerBU.filter(element => {
        return path === element.path;
      });
    }

    if (oldModule && oldModule.length > 0) {
      return oldModule[0].id;
    } else if (oldBUModule && oldBUModule.length > 0) {
      return oldBUModule[0].id;
    } else {
      var nID = generateId();
      var enterModule = {
        id: nID,
        path: path
      };

      if (!global.CRN_BUILD_COMMON) {
        if (path.indexOf("__prelude__") == -1 &&
          path.indexOf("/node_modules/react-native/Libraries/polyfills") == -1 &&
          path.indexOf("require-/") == -1 &&
          path.indexOf("source-map") == -1 &&
          path.indexOf("/node_modules/metro/src/lib/polyfills/") == -1) {
          mappingContainerBU.push(enterModule);
        }
      } else {
        mappingContainer.push(enterModule);
      }

      return nID;
    }
  };
}

createModuleIdFactory.getBaseMapping = function () {
  return mappingContainer;
};

createModuleIdFactory.getBUMapping = function () {
  return mappingContainerBU;
};


module.exports = createModuleIdFactory;

