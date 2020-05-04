'use strict';

let lib = `(function() {
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
const objArrToString = (args) => {
    for(let i = 0 ; i < args.length ; i++) {
        if(Array.isArray(args[i]))
    }
    return Promise.resolve(args);
}
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
const originF = {};
for(let i = 0 ; i < Number.MAX_VALUE ; i++) {
    const v = 'flexAnd' + i;
    if(originF[v] === undefined) originF[v] = window[v];
    if(originF[v] === undefined) break;
    Object.keys(originF[v]).forEach(k => {
        if($flex[k] === undefined) {
            $flex[k] =
            function(...args) {
                return new Promise(resolve => {
                    const call = originF[v][k](...args);
                    if(typeof call.job === "function") {
                        genFName().then(name => {
                            window[name] = (r) => {
                                resolve(r);
                                delete window[name];
                            };
                            call.job(name);
                        });
                    } else {
                        resolve(call);
                    }
                });
            }
        }
    });
    delete window[v];
}
const frames = window.frames;
for(let i = 0 ; i < frames.length; i++) {
    frames[i].Function("let lib=" + script + ";window.Function(lib)(),lib=void 0;")();
}
setTimeout(() => {
    if(typeof window.onFlexLoad === 'function') {
        window.onFlexLoad()
    }
},0)
})();`;
window.Function(lib)();
lib = undefined;
