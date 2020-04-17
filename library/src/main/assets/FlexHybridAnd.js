'use strict';

let lib = `(function flexInit() {
const events = [];
window.$flex = {
    version: '0.1',
    load: false,
    addEvent: function(event, callback) { events.push({ e: event, c: callback }) },
    web: {}
};
const originF = {};
for(let i = 0 ; i < Number.MAX_VALUE ; i++) {
    const v = 'flexAnd' + i;
    originF[v] = window[v];
    if(originF[v] === undefined) {
        $flex.load = true;
        break;
    }
    Object.keys(originF[v]).forEach(k => {
        if(Object.keys($flex).findIndex(n => n === k) === -1) {
            $flex[k] =
            function(...args) {
                return new Promise(resolve => {
                    const call = originF[v][k](...args);
                    const name = 'f' + Math.random().toString(10).substr(2,8);
                    window[name] = (r) => {
                        resolve(r);
                        window[name] = undefined;
                    };
                    call.job(name);
                });
            }
        }
    });
    window[v] = undefined;
}
const frames = window.frames;
for(let i = 0 ; i < frames.length; i++) {
    frames[i].Function("let script = " + lib + ";window.Function(script)();script=undefined;")
}
})();`;
window.Function(lib)();
lib = undefined;