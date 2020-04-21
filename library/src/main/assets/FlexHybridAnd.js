'use strict';

let lib = `(function() {
const script = lib;
const events = [];
window.$flex = {};
Object.defineProperties($flex,
    {
        version: { value: '0.1.2', writable: false },
        addEvent: { value: function(event, callback) { events.push({ e: event, c: callback }) }, writable: false },
        init: { value: function() { window.Function(script)(); }, writable: false },
        web: { value: {}, writable: false }
    }
)
const originF = {};
for(let i = 0 ; i < Number.MAX_VALUE ; i++) {
    const v = 'flexAnd' + i;
    originF[v] = window[v];
    if(originF[v] === undefined) {
        break;
    }
    Object.keys(originF[v]).forEach(k => {
        if(Object.keys($flex).findIndex(n => n === k) === -1) {
            $flex[k] =
            function(...args) {
                return new Promise(resolve => {
                    const call = originF[v][k](...args);
                    if(typeof call.job === "function") {
                        const name = 'f' + Math.random().toString(10).substr(2,8);
                        window[name] = (r) => {
                            resolve(r);
                            window[name] = undefined;
                        };
                        call.job(name);
                    } else {
                        resolve(call);
                    }
                });
            }
        }
    });
    window[v] = undefined;
}
const frames = window.frames;
for(let i = 0 ; i < frames.length; i++) {
    frames[i].Function("let lib=" + script + ";window.Function(lib)();lib=undefined;")();
}
})();`;
window.Function(lib)();
lib = undefined;