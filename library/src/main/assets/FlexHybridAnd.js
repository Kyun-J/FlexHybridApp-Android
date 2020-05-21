'use strict';
let lib = `(function() {
const keys = keysfromAnd;
const script = lib;
const listeners = [];
const events = [''];
window.$flex = {};
Object.defineProperties($flex,
    {
        version: { value: '0.2.2.1', writable: false, enumerable: true },
        //addEventListener: { value: function(event, callback) { listeners.push({ e: event, c: callback }) }, writable: false },
        web: { value: {}, writable: false, enumerable: true },
        flex: { value: {}, writable: false, enumerable: false }
    }
)
const genFName = () => {
    const name = 'f' + Math.random().toString(10).substr(2,8);
    if(window[name] === undefined) {
        return Promise.resolve(name)
    } else {
        return Promise.resolve(genFName())
    }
}
const triggerEventListener = (name, val) => {
    listeners.forEach(element => {
        if(element.e === name && typeof element.c === 'function') {
            element.c(val);
        }
    });
}
const define = window.flexdefine;
delete window.flexdefine;
keys.forEach(key => {
    if($flex[key] === undefined) {
        Object.defineProperty($flex, key, {
            value :
            function(...args) {
                return new Promise(resolve => {
                    genFName().then(name => {
                        $flex.flex[name] = (r) => {
                            resolve(r);
                            delete $flex.flex[name];
                        };
                        define.flexInterface(JSON.stringify({intName:key,funName:name,arguments:args}));
                    });
                });
            },
            writable: false,
            enumerable: true
        });
    }
});
const frames = window.frames;
for(let i = 0 ; i < frames.length; i++) {
    frames[i].Function('let lib=' + script + ';window.Function(lib)(),lib=void 0;')();
}
setTimeout(() => {
    if(typeof window.onFlexLoad === 'function') {
        window.onFlexLoad();
    }
},0);
})();`;
window.Function(lib)();
lib = undefined;