'use strict';
let k = keysfromAnd;
let lib = `(function() {
const keys = k;
const script = lib;
const listeners = [];
window.$flex = {};
Object.defineProperties($flex,
    {
        version: { value: '0.2', writable: false },
        addEventListener: { value: function(event, callback) { listeners.push({ e: event, c: callback }) }, writable: false },
        init: { value: function() { window.Function(script)(); }, writable: false },
        web: { value: {}, writable: false }
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
keys.forEach(key => {
    if($flex[key] === undefined) {
        $flex[key] =
        function(...args) {
            return new Promise(resolve => {
                genFName().then(name => {
                    window[name] = (r) => {
                        resolve(r);
                        delete window[name];
                    };
                    window.flexdefine.flexInterface(JSON.stringify({intName:key,funName:name,arguments:args}));
                });
            });
        }
    }
});
const frames = window.frames;
for(let i = 0 ; i < frames.length; i++) {
    frames[i].Function("let k=" + keys + "let lib=" + script + ";window.Function(lib)(),lib=void 0,k=void 0;")();
}
setTimeout(() => {
    if(typeof window.onFlexLoad === 'function') {
        window.onFlexLoad()
    }
},0);
})();`;
window.Function(lib)();
lib = undefined;
k = undefined;
