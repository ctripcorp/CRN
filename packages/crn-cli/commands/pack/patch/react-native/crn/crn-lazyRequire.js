/**
 * lazyRequire
 */
global.lazyRequire = lazyRequire;
function lazyRequire(requirePath) {
    var lazy = {
        __lazyRequireFlag: true,
        __lazy_module_id__ : requirePath,
        load : function() {
            var module = global.__r(this.__lazy_module_id__);
            return module;
        }
    };
    return lazy;
}