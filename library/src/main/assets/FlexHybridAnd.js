'use strict';

let lib = `(function() {
const script = lib;
const events = [];
window.$flex = {};
Object.defineProperties($flex,
    {
        version: { value: '0.1.3', writable: false },
        addEventListener: { value: function(event, callback) { events.push({ e: event, c: callback }) }, writable: false },
        init: { value: function() { window.Function(script)(); }, writable: false },
        web: { value: {}, writable: false }
    }
)
const originF = {};
const genFName = () => {
    const name = 'f' + Math.random().toString(10).substr(2,8);
    if(window[name] === undefined) {
        return Promise.resolve(name)
    } else {
        return Promise.resolve(genFName())
    }
}
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
                                window[name] = undefined;
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
    window[v] = undefined;
}
const frames = window.frames;
for(let i = 0 ; i < frames.length; i++) {
    frames[i].Function("let lib=" + script + ";window.Function(lib)(),lib=void 0;")();
}
})();`;
window.Function(lib)();
lib = undefined;