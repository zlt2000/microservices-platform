/**
 * 路由组件
 * q.js-1.2 <https://github.com/itorr/q.js>
 */
var Q = function (W, D, HTML, hash, view, arg, _arg, i, index, Regex, key, Q) {
    HTML = D.documentElement;
    Regex = [];
    key = '!';
    onhashchange = function () {
        Q.hash = hash = location.hash.substring(key.length + 1);

        arg = hash.split(/\?/g).shift();

        arg = arg.split('/');

        i = Regex.length;
        while (i--)
            if (_arg = hash.match(Regex[i])) {
                arg = _arg;
                arg[0] = Regex[i];
                break;
            }


        if (!Q[arg[0]]) // default
            arg[0] = index;

        if (Q.pop)
            Q.pop.apply(W, arg);

        Q.lash = view = arg.shift();

        HTML.setAttribute('view', view);

        Q[view].apply(W, arg);
    };

    if (!'onhashchange' in W) {
        Q.path = location.hash;
        setInterval(function () {
            if (Q.path != location.hash) {
                onhashchange();
                Q.path = location.hash;
            }
        }, 100);
    }

    Q = {
        init: function (o) {

            if (o.key !== undefined)
                key = o.key;

            index = o.index || 'V';

            if (o.pop && typeof o.pop == 'function')
                Q.pop = o.pop;

            onhashchange();

            return this
        },
        reg: function (r, u) {
            if (!r)
                return;

            if (u == undefined)
                u = function () {
                };

            if (r instanceof RegExp) { //正则注册
                Q[r] = u;
                Regex.push(r);
            } else if (r instanceof Array) { //数组注册
                for (var i in r) {
                    this.reg.apply(this, [].concat(r[i]).concat(u));
                }
            } else if (typeof r == 'string') { //关键字注册
                if (typeof u == 'function')
                    Q[r] = u;
                else if (typeof u == 'string' && Q[u])
                    Q[r] = Q[u];
            }

            return this
        },
        V: function () {
            console.log('q.js <https://github.com/itorr/q.js> 2014/12/28');
            return this
        },
        go: function (u) {
            location.hash = '#' + key + u;
            return this
        },
        refresh: function () {
            onhashchange();
        }
    };
    return Q;
}(this, document);
