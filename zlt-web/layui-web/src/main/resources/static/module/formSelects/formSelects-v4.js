'use strict';

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

/**
 * name: formSelects
 * 基于Layui Select多选
 * version: 4.0.0.formSelects
 * http://sun.faysunshine.com/layui/formSelects-v4/dist/formSelects-v4.js
 */
(function (layui, window, factory) {
	if ((typeof exports === 'undefined' ? 'undefined' : _typeof(exports)) === 'object') {
		// 支持 CommonJS
		module.exports = factory();
	} else if (typeof define === 'function' && define.amd) {
		// 支持 AMD
		define(factory);
	} else if (window.layui && layui.define) {
		//layui加载
		layui.define(['jquery'], function (exports) {
			exports('formSelects', factory());
		});
	} else {
		window.formSelects = factory();
	}
})(typeof layui == 'undefined' ? null : layui, window, function () {
	var v = '4.0.0.formSelects',
	    NAME = 'xm-select',
	    PNAME = 'xm-select-parent',
	    INPUT = 'xm-select-input',
	    TDIV = 'xm-select--suffix',
	    THIS = 'xm-select-this',
	    LABEL = 'xm-select-label',
	    SEARCH = 'xm-select-search',
	    CREATE = 'xm-select-create',
	    CREATE_LONG = 'xm-select-create-long',
	    MAX = 'xm-select-max',
	    SKIN = 'xm-select-skin',
	    DIRECTION = "xm-select-direction",
	    HEIGHT = 'xm-select-height',
	    DISABLED = 'xm-dis-disabled',
	    DIS = 'xm-select-dis',
	    TEMP = 'xm-select-temp',
	    RADIO = 'xm-select-radio',
	    LINKAGE = 'xm-select-linkage',
	    DL = 'xm-select-dl',
	    HIDE_INPUT = 'xm-hide-input',
	    SANJIAO = 'xm-select-sj',
	    ICON_CLOSE = 'xm-icon-close',
	    FORM_TITLE = 'xm-select-title',
	    FORM_SELECT = 'xm-form-select',
	    FORM_SELECTED = 'xm-form-selected',
	    FORM_NONE = 'xm-select-none',
	    FORM_EMPTY = 'xm-select-empty',
	    FORM_INPUT = 'xm-input',
	    FORM_SELECT_TIPS = 'xm-select-tips',
	    CHECKBOX_YES = 'xm-icon-yes',
	    CZ = 'xm-cz',
	    CZ_GROUP = 'xm-cz-group',
	    TIPS = '请选择',
	    data = {},
	    events = {
		on: {},
		filter: {},
		maxTips: {}
	},
	    ajax = {
		type: 'get',
		header: {},
		first: true,
		data: {},
		searchUrl: '',
		searchName: 'keyword',
		searchVal: null,
		keyName: 'name',
		keyVal: 'value',
		keySel: 'selected',
		keyDis: 'disabled',
		keyChildren: 'children',
		dataType: '',
		delay: 500,
		beforeSuccess: null,
		success: null,
		error: null,
		beforeSearch: null,
		clearInput: false
	},
	    quickBtns = [{ icon: 'iconfont icon-quanxuan', name: '全选', click: function click(id, cm) {
			cm.selectAll(id, true, true);
		} }, { icon: 'iconfont icon-qingkong', name: '清空', click: function click(id, cm) {
			cm.removeAll(id, true, true);
		} }, { icon: 'iconfont icon-fanxuan', name: '反选', click: function click(id, cm) {
			cm.reverse(id, true, true);
		} }, { icon: 'iconfont icon-pifu', name: '换肤', click: function click(id, cm) {
			cm.skin(id);
		} }],
	    $ = window.$ || window.layui && window.layui.jquery,
	    $win = $(window),
	    ajaxs = {},
	    FormSelects = function FormSelects(options) {
		var _this = this;

		this.config = {
			name: null, //xm-select="xxx"
			max: null,
			maxTips: function maxTips(vals, val, max) {
				var ipt = $('[xid="' + _this.config.name + '"]').prev().find('.' + NAME);
				if (ipt.parents('.layui-form-item[pane]').length) {
					ipt = ipt.parents('.layui-form-item[pane]');
				}
				ipt.attr('style', 'border-color: red !important');
				setTimeout(function () {
					ipt.removeAttr('style');
				}, 300);
			},
			init: null, //初始化的选择值,
			on: null, //select值发生变化
			filter: function filter(id, inputVal, val, isDisabled) {
				return val.name.indexOf(inputVal) == -1;
			},
			clearid: -1,
			direction: 'auto',
			height: null,
			isEmpty: false,
			btns: [quickBtns[0], quickBtns[1], quickBtns[2]]
		};
		this.select = null;
		this.values = [];
		$.extend(true, this.config, options);
	};

	//一些简单的处理方法
	var Common = function Common() {
		this.loadingCss();
		this.appender();
		this.init();
		this.on();
		this.initVal();
		this.onreset();
		this.listening();
	};

	Common.prototype.appender = function () {
		//针对IE做的一些拓展
		if (!Array.prototype.map) {
			Array.prototype.map = function (callback, thisArg) {
				var T,
				    A,
				    k,
				    O = Object(this),
				    len = O.length >>> 0;
				if (thisArg) {
					T = thisArg;
				}
				A = new Array(len);
				k = 0;
				while (k < len) {
					var kValue, mappedValue;
					if (k in O) {
						kValue = O[k];
						mappedValue = callback.call(T, kValue, k, O);
						A[k] = mappedValue;
					}
					k++;
				}
				return A;
			};
		}
		if (!Array.prototype.forEach) {
			Array.prototype.forEach = function forEach(callback, thisArg) {
				var T, k;
				if (this == null) {
					throw new TypeError("this is null or not defined");
				}
				var O = Object(this);
				var len = O.length >>> 0;
				if (typeof callback !== "function") {
					throw new TypeError(callback + " is not a function");
				}
				if (arguments.length > 1) {
					T = thisArg;
				}
				k = 0;
				while (k < len) {
					var kValue;
					if (k in O) {

						kValue = O[k];
						callback.call(T, kValue, k, O);
					}
					k++;
				}
			};
		}
	};

	Common.prototype.init = function (target) {
		var _this2 = this;

		//初始化页面上已有的select
		$(target ? target : 'select[' + NAME + ']').each(function (index, select) {
			var othis = $(select),
			    id = othis.attr(NAME),
			    hasRender = othis.next('.layui-form-select'),
			    disabled = select.disabled,
			    max = othis.attr(MAX) - 0,
			    isSearch = othis.attr(SEARCH) != undefined,
			    searchUrl = isSearch ? othis.attr(SEARCH) : null,
			    isCreate = othis.attr(CREATE) != undefined,
			    isRadio = othis.attr(RADIO) != undefined,
			    skin = othis.attr(SKIN),
			    direction = othis.attr(DIRECTION),
			    optionsFirst = select.options[0],
			    height = othis.attr(HEIGHT),
			    formname = othis.attr('name'),
			    layverify = othis.attr('lay-verify'),
			    placeholder = optionsFirst ? optionsFirst.value ? TIPS : optionsFirst.innerHTML || TIPS : TIPS,
			    value = othis.find('option[selected]').toArray().map(function (option) {
				//获取已选中的数据
				return {
					name: option.innerHTML,
					val: option.value
				};
			}),
			    fs = new FormSelects();
			data[id] = fs;
			//先取消layui对select的渲染
			hasRender[0] && hasRender.remove();

			//包裹一个div
			othis.wrap('<div class="' + PNAME + '"></div>');

			//构造渲染div
			var dinfo = _this2.renderSelect(id, placeholder, select);
			var heightStyle = height ? 'style="height: ' + height + ';"' : '';
			var inputHtml = height ? ['<div class="' + LABEL + '" style="margin-right: 50px;"></div>', '<input type="text" fsw class="' + FORM_INPUT + ' ' + INPUT + '" ' + (isSearch ? '' : 'style="display: none;"') + ' autocomplete="off" debounce="0" style="position: absolute;right: 10px;top: 3px;"/>'] : ['<div class="' + LABEL + '">', '<input type="text" fsw class="' + FORM_INPUT + ' ' + INPUT + '" ' + (isSearch ? '' : 'style="display: none;"') + ' autocomplete="off" debounce="0" />', '</div>'];
			var reElem = $('<div class="' + FORM_SELECT + '" ' + SKIN + '="' + skin + '">\n\t\t\t\t\t<input type="hidden" class="' + HIDE_INPUT + '" value="" name="' + formname + '" lay-verify="' + layverify + '"/>\n\t\t\t\t\t<div class="' + FORM_TITLE + ' ' + (disabled ? DIS : '') + '">\n\t\t\t\t\t\t<div class="' + FORM_INPUT + ' ' + NAME + '" ' + heightStyle + '>\n\t\t\t\t\t\t\t' + inputHtml.join('') + '\n\t\t\t\t\t\t\t<i class="' + SANJIAO + '"></i>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t\t<div class="' + TDIV + '">\n\t\t\t\t\t\t\t<input type="text" autocomplete="off" placeholder="' + placeholder + '" readonly="readonly" unselectable="on" class="' + FORM_INPUT + '">\n\t\t\t\t\t\t</div>\n\t\t\t\t\t\t<div></div>\n\t\t\t\t\t</div>\n\t\t\t\t\t<dl xid="' + id + '" class="' + DL + ' ' + (isRadio ? RADIO : '') + '">' + dinfo + '</dl>\n\t\t\t\t</div>');
			othis.after(reElem);
			fs.select = othis.remove(); //去掉layui.form.render
			fs.values = value;
			fs.config.name = id;
			fs.config.init = value.concat([]);
			fs.config.direction = direction;
			fs.config.height = height;
			fs.config.radio = isRadio;

			if (max) {
				//有最大值
				fs.config.max = max;
			}

			//如果可搜索, 加上事件
			if (isSearch) {
				reElem.find('.' + INPUT).on('input propertychange', function (e) {
					var input = e.target,
					    inputValue = $.trim(input.value),
					    keyCode = e.keyCode;
					if (keyCode === 9 || keyCode === 13 || keyCode === 37 || keyCode === 38 || keyCode === 39 || keyCode === 40) {
						return false;
					}

					//过滤一下tips
					_this2.changePlaceHolder($(input));

					var ajaxConfig = ajaxs[id] ? ajaxs[id] : ajax;
					searchUrl = ajaxConfig.searchUrl || searchUrl;
					//如果开启了远程搜索
					if (searchUrl) {
						if (ajaxConfig.searchVal) {
							inputValue = ajaxConfig.searchVal;
							ajaxConfig.searchVal = '';
						}
						if (!ajaxConfig.beforeSearch || ajaxConfig.beforeSearch && ajaxConfig.beforeSearch instanceof Function && ajaxConfig.beforeSearch(id, searchUrl, inputValue)) {
							var delay = ajaxConfig.delay;
							if (ajaxConfig.first) {
								ajaxConfig.first = false;
								delay = 10;
							}
							clearTimeout(fs.clearid);
							fs.clearid = setTimeout(function () {
								reElem.find('dl > *:not(.' + FORM_SELECT_TIPS + ')').remove();
								reElem.find('dd.' + FORM_NONE).addClass(FORM_EMPTY).text('请求中');
								_this2.ajax(id, searchUrl, inputValue, false, null, true);
							}, delay);
						}
					} else {
						reElem.find('dl .layui-hide').removeClass('layui-hide');
						//遍历选项, 选择可以显示的值
						reElem.find('dl dd:not(.' + FORM_SELECT_TIPS + ')').each(function (idx, item) {
							var _item = $(item);
							var searchFun = data[id].config.filter || events.filter[id];
							if (searchFun && searchFun(id, inputValue, {
								name: _item.find('span').text(),
								val: _item.attr('lay-value')
							}, _item.hasClass(DISABLED)) == true) {
								_item.addClass('layui-hide');
							}
						});
						//控制分组名称
						reElem.find('dl dt').each(function (index, item) {
							if (!$(item).nextUntil('dt', ':not(.layui-hide)').length) {
								$(item).addClass('layui-hide');
							}
						});
						//动态创建
						_this2.create(id, isCreate, inputValue);
						var shows = reElem.find('dl dd:not(.' + FORM_SELECT_TIPS + '):not(.layui-hide)');
						if (!shows.length) {
							reElem.find('dd.' + FORM_NONE).addClass(FORM_EMPTY).text('无匹配项');
						} else {
							reElem.find('dd.' + FORM_NONE).removeClass(FORM_EMPTY);
						}
					}
				});
				if (searchUrl) {
					//触发第一次请求事件
					_this2.triggerSearch(reElem, true);
				}
			}
		});
	};

	Common.prototype.isArray = function (obj) {
		return Object.prototype.toString.call(obj) == "[object Array]";
	};

	Common.prototype.triggerSearch = function (div, isCall) {
		(div ? [div] : $('.' + FORM_SELECT).toArray()).forEach(function (reElem, index) {
			reElem = $(reElem);
			var id = reElem.find('dl').attr('xid');
			if (id && data[id] && data[id].config.isEmpty || isCall) {
				var obj_caller = reElem.find('.' + INPUT)[0];
				if (document.createEventObject) {
					obj_caller.fireEvent("onchange");
				} else {
					var evt = document.createEvent("HTMLEvents");
					evt.initEvent("input", false, true);
					obj_caller.dispatchEvent(evt);
				}
			}
		});
	};

	Common.prototype.ajax = function (id, searchUrl, inputValue, isLinkage, linkageWidth, isSearch) {
		var _this3 = this;

		var reElem = $('.' + PNAME + ' dl[xid="' + id + '"]').parents('.' + FORM_SELECT);
		if (!reElem[0] || !searchUrl) {
			return;
		}

		var ajaxConfig = ajaxs[id] ? ajaxs[id] : ajax;
		var ajaxData = $.extend(true, {}, ajaxConfig.data);
		ajaxData[ajaxConfig.searchName] = inputValue;
		ajaxData['_'] = Date.now();
		$.ajax({
			type: ajaxConfig.type,
			headers: ajaxConfig.header,
			url: searchUrl,
			data: ajaxConfig.dataType == 'json' ? JSON.stringify(ajaxData) : ajaxData,
			success: function success(res) {
				if (typeof res == 'string') {
					res = JSON.parse(res);
				}
				ajaxConfig.beforeSuccess && ajaxConfig.beforeSuccess instanceof Function && (res = ajaxConfig.beforeSuccess(id, searchUrl, inputValue, res));
				if (_this3.isArray(res)) {
					res = {
						code: 0,
						msg: "",
						data: res
					};
				}
				if (res.code != 0) {
					reElem.find('dd.' + FORM_NONE).addClass(FORM_EMPTY).text(res.msg);
				} else {
					reElem.find('dd.' + FORM_NONE).removeClass(FORM_EMPTY);
					//获得已选择的values
					_this3.renderData(id, res.data, isLinkage, linkageWidth, isSearch);
					data[id].config.isEmpty = res.data.length == 0;
				}
				ajaxConfig.success && ajaxConfig.success instanceof Function && ajaxConfig.success(id, searchUrl, inputValue, res);
			},
			error: function error(err) {
				reElem.find('dd[lay-value]:not(.' + FORM_SELECT_TIPS + ')').remove();
				reElem.find('dd.' + FORM_NONE).addClass(FORM_EMPTY).text('服务异常');
				ajaxConfig.error && ajaxConfig.error instanceof Function && ajaxConfig.error(id, searchUrl, inputValue, err);
			}
		});
	};

	Common.prototype.renderData = function (id, dataArr, linkage, linkageWidth, isSearch) {
		var _this4 = this;

		if (linkage) {
			var _ret = function () {
				//渲染多级联动
				var result = [],
				    index = 0,
				    temp = { "0": dataArr },
				    ajaxConfig = ajaxs[id] ? ajaxs[id] : ajax;

				var _loop = function _loop() {
					var group = result[index++] = [],
					    _temp = temp;
					temp = {};
					$.each(_temp, function (pid, arr) {
						$.each(arr, function (idx, item) {
							var val = {
								pid: pid,
								name: item[ajaxConfig.keyName],
								val: item[ajaxConfig.keyVal]
							};
							group.push(val);
							var children = item[ajaxConfig.keyChildren];
							if (children && children.length) {
								temp[val.val] = children;
							}
						});
					});
				};

				do {
					_loop();
				} while (Object.getOwnPropertyNames(temp).length);

				var reElem = $('.' + PNAME + ' dl[xid="' + id + '"]').parents('.' + FORM_SELECT);
				var html = ['<div class="xm-select-linkage">'];

				$.each(result, function (idx, arr) {
					var groupDiv = ['<div style="left: ' + (linkageWidth - 0) * idx + 'px;" class="xm-select-linkage-group xm-select-linkage-group' + (idx + 1) + ' ' + (idx != 0 ? 'xm-select-linkage-hide' : '') + '">'];
					$.each(arr, function (idx2, item) {
						var span = '<li title="' + item.name + '" pid="' + item.pid + '" value="' + item.val + '"><span>' + item.name + '</span></li>';
						groupDiv.push(span);
					});
					groupDiv.push('</div>');
					html = html.concat(groupDiv);
				});
				//			<li class="xm-select-this xm-select-active"><span>123</span></li>
				html.push('<div style="clear: both; height: 288px;"></div>');
				html.push('</div>');
				reElem.find('dl').html(html.join(''));
				reElem.find('.' + INPUT).css('display', 'none'); //联动暂时不支持搜索
				return {
					v: void 0
				};
			}();

			if ((typeof _ret === 'undefined' ? 'undefined' : _typeof(_ret)) === "object") return _ret.v;
		}

		var reElem = $('.' + PNAME + ' dl[xid="' + id + '"]').parents('.' + FORM_SELECT);
		var ajaxConfig = ajaxs[id] ? ajaxs[id] : ajax;
		var pcInput = reElem.find('.' + TDIV + ' input');

		var values = [];
		reElem.find('dl').html(this.renderSelect(id, pcInput.attr('placeholder') || pcInput.attr('back'), dataArr.map(function (item) {
			if (item[ajaxConfig.keySel]) {
				values.push({
					name: item[ajaxConfig.keyName],
					val: item[ajaxConfig.keyVal]
				});
			}
			return {
				innerHTML: item[ajaxConfig.keyName],
				value: item[ajaxConfig.keyVal],
				sel: item[ajaxConfig.keySel],
				disabled: item[ajaxConfig.keyDis],
				type: item.type,
				name: item.name
			};
		})));

		var label = reElem.find('.' + LABEL);
		var dl = reElem.find('dl[xid]');
		if (isSearch) {
			//如果是远程搜索, 这里需要判重
			var oldVal = data[id].values;
			oldVal.forEach(function (item, index) {
				dl.find('dd[lay-value="' + item.val + '"]').addClass(THIS);
			});
			values.forEach(function (item, index) {
				if (_this4.indexOf(oldVal, item) == -1) {
					_this4.addLabel(id, label, item);
					dl.find('dd[lay-value="' + item.val + '"]').addClass(THIS);
					oldVal.push(item);
				}
			});
		} else {
			values.forEach(function (item, index) {
				_this4.addLabel(id, label, item);
				dl.find('dd[lay-value="' + item.val + '"]').addClass(THIS);
			});
			data[id].values = values;
		}
		this.commonHanler(id, label);
	};

	Common.prototype.create = function (id, isCreate, inputValue) {
		if (isCreate && inputValue) {
			var fs = data[id],
			    dl = $('[xid="' + id + '"]'),
			    tips = dl.find('dd.' + FORM_SELECT_TIPS + ':first'),
			    tdd = null,
			    temp = dl.find('dd.' + TEMP);
			dl.find('dd:not(.' + FORM_SELECT_TIPS + '):not(.' + TEMP + ')').each(function (index, item) {
				if (inputValue == $(item).find('span').text()) {
					tdd = item;
				}
			});
			if (!tdd) {
				//如果不存在, 则创建
				if (temp[0]) {
					temp.attr('lay-value', inputValue);
					temp.find('span').text(inputValue);
					temp.removeClass('layui-hide');
				} else {
					tips.after($(this.createDD({
						innerHTML: inputValue,
						value: Date.now()
					}, TEMP + ' ' + CREATE_LONG)));
				}
			}
		} else {
			$('[xid=' + id + '] dd.' + TEMP).remove();
		}
	};

	Common.prototype.createDD = function (item, clz) {
		return '<dd lay-value="' + item.value + '" class="' + (item.disabled ? DISABLED : '') + ' ' + (clz ? clz : '') + '">\n\t\t\t\t\t<div class="xm-unselect xm-form-checkbox ' + (item.disabled ? 'layui-checkbox-disbaled ' + DISABLED : '') + '" lay-skin="primary">\n\t\t\t\t\t\t<span>' + $.trim(item.innerHTML) + '</span>\n\t\t\t\t\t\t<i class="' + CHECKBOX_YES + '"></i>\n\t\t\t\t\t</div>\n\t\t\t\t</dd>';
	};

	Common.prototype.createQuickBtn = function (obj, right) {
		return '<div class="' + CZ + '" method="' + obj.name + '" title="' + obj.name + '" ' + (right ? 'style="margin-right: ' + right + '"' : '') + '><i class="' + obj.icon + '"></i><span>' + obj.name + '</span></div>';
	};

	Common.prototype.renderBtns = function (id, show, right) {
		var _this5 = this;

		var quickBtn = [];
		var dl = $('dl[xid="' + id + '"]');
		quickBtn.push('<div class="' + CZ_GROUP + '" show="' + show + '" style="max-width: ' + (dl.prev().width() - 54) + 'px;">');
		$.each(data[id].config.btns, function (index, item) {
			quickBtn.push(_this5.createQuickBtn(item, right));
		});
		quickBtn.push('</div>');
		quickBtn.push(this.createQuickBtn({ icon: 'iconfont icon-caidan', name: '' }));
		return quickBtn.join('');
	};

	Common.prototype.renderSelect = function (id, tips, select) {
		var _this6 = this;

		var arr = [];
		if (data[id].config.btns.length) {
			setTimeout(function () {
				var dl = $('dl[xid="' + id + '"]');
				dl.find('.' + CZ_GROUP).css('max-width', dl.prev().width() - 54 + 'px');
			}, 10);
			arr.push(['<dd lay-value="" class="' + FORM_SELECT_TIPS + '" style="background-color: #FFF!important;">', this.renderBtns(id, null, '30px'), '</dd>'].join(''));
		} else {
			arr.push('<dd lay-value="" class="' + FORM_SELECT_TIPS + '">' + tips + '</dd>');
		}
		if (this.isArray(select)) {
			$(select).each(function (index, item) {
				if (item.type === 'optgroup') {
					arr.push('<dt>' + item.name + '</dt>');
				} else {
					arr.push(_this6.createDD(item));
				}
			});
		} else {
			$(select).find('*').each(function (index, item) {
				if (item.tagName.toLowerCase() == 'option' && index == 0 && !item.value) {
					return;
				}
				if (item.tagName.toLowerCase() === 'optgroup') {
					arr.push('<dt>' + item.label + '</dt>');
				} else {
					arr.push(_this6.createDD(item));
				}
			});
		}
		arr.push('<dt style="display:none;"> </dt>');
		arr.push('<dd class="' + FORM_SELECT_TIPS + ' ' + FORM_NONE + ' ' + (arr.length === 2 ? FORM_EMPTY : '') + '">\u6CA1\u6709\u9009\u9879</dd>');
		return arr.join('');
	};

	Common.prototype.on = function () {
		var _this7 = this;

		//事件绑定
		this.one();

		$(document).on('click', function (e) {
			if (!$(e.target).parents('.' + FORM_TITLE)[0]) {
				//清空input中的值
				$('.' + INPUT).val('');
				$('.' + PNAME + ' dl .layui-hide').removeClass('layui-hide');
				$('.' + PNAME + ' dl dd.' + TEMP).remove();
				_this7.triggerSearch();
			}
			$('.' + PNAME + ' .' + FORM_SELECTED).removeClass(FORM_SELECTED);
		});
	};

	Common.prototype.one = function (target) {
		var _this8 = this;

		//一次性事件绑定
		$(target ? target : document).find('.' + FORM_TITLE).off('click').on('click', function (e) {
			var othis = $(e.target),
			    title = othis.is(FORM_TITLE) ? othis : othis.parents('.' + FORM_TITLE),
			    dl = title.next(),
			    id = dl.attr('xid');

			//清空非本select的input val
			$('dl[xid]').not(dl).prev().find('.' + INPUT).val('');
			$('dl[xid]').not(dl).find('dd.layui-hide').removeClass('layui-hide');

			//如果是disabled select
			if (title.hasClass(DIS)) {
				return false;
			}
			//如果点击的是右边的三角或者只读的input
			if (othis.is('.' + SANJIAO) || othis.is('.' + INPUT + '[readonly]')) {
				_this8.changeShow(title, !title.parents('.' + FORM_SELECT).hasClass(FORM_SELECTED));
				return false;
			}
			//如果点击的是input的右边, focus一下
			if (title.find('.' + INPUT + ':not(readonly)')[0]) {
				var input = title.find('.' + INPUT),
				    epos = { x: e.pageX, y: e.pageY },
				    pos = _this8.getPosition(title[0]),
				    width = title.width();
				while (epos.x > pos.x) {
					if ($(document.elementFromPoint(epos.x, epos.y)).is(input)) {
						input.focus();
						_this8.changeShow(title, true);
						return false;
					}
					epos.x -= 50;
				}
			}

			//如果点击的是可搜索的input
			if (othis.is('.' + INPUT)) {
				_this8.changeShow(title, true);
				return false;
			}
			//如果点击的是x按钮
			if (othis.is('i[fsw="' + NAME + '"]')) {
				var val = {
					name: othis.prev().text(),
					val: othis.parent().attr("value")
				},
				    dd = dl.find('dd[lay-value=\'' + val.val + '\']');
				if (dd.hasClass(DISABLED)) {
					//如果是disabled状态, 不可选, 不可删
					return false;
				}
				_this8.handlerLabel(id, dd, false, val);
				return false;
			}

			_this8.changeShow(title, !title.parents('.' + FORM_SELECT).hasClass(FORM_SELECTED));
			return false;
		});
		$(target ? target : document).find('dl.' + DL).off('click').on('click', function (e) {
			var othis = $(e.target);
			if (othis.is('.' + LINKAGE) || othis.parents('.' + LINKAGE)[0]) {
				//linkage的处理
				othis = othis.is('li') ? othis : othis.parents('li');
				var _group = othis.parents('.xm-select-linkage-group'),
				    _id = othis.parents('dl').attr('xid');
				//激活li
				_group.find('.xm-select-active').removeClass('xm-select-active');
				othis.addClass('xm-select-active');
				//激活下一个group, 激活前显示对应数据
				_group.nextAll('.xm-select-linkage-group').addClass('xm-select-linkage-hide');
				var nextGroup = _group.next('.xm-select-linkage-group');
				nextGroup.find('li').addClass('xm-select-linkage-hide');
				nextGroup.find('li[pid="' + othis.attr('value') + '"]').removeClass('xm-select-linkage-hide');
				//如果没有下一个group, 或没有对应的值
				if (!nextGroup[0] || nextGroup.find('li:not(.xm-select-linkage-hide)').length == 0) {
					var vals = [],
					    index = 0,
					    _isAdd = !othis.hasClass('xm-select-this');
					if (data[_id].config.radio) {
						othis.parents('.xm-select-linkage').find('.xm-select-this').removeClass('xm-select-this');
					}
					do {
						vals[index++] = {
							name: othis.find('span').text(),
							val: othis.attr('value')
							/*isAdd ? (
       	othis.addClass('xm-select-this')
       ) : (
       	!othis.parent('.xm-select-linkage-group').next().find(`li[pid="${othis.attr('value')}"].xm-select-this`).length && othis.removeClass('xm-select-this')
       );*/
						};othis = othis.parents('.xm-select-linkage-group').prev().find('li[value="' + othis.attr('pid') + '"]');
					} while (othis.length);
					vals.reverse();
					var val = {
						name: vals.map(function (item) {
							return item.name;
						}).join('/'),
						val: vals.map(function (item) {
							return item.val;
						}).join('/')
					};
					_this8.handlerLabel(_id, null, _isAdd, val);
				} else {
					nextGroup.removeClass('xm-select-linkage-hide');
				}
				return false;
			} //xm-select-this xm-select-active

			if (othis.is('dt') || othis.is('dl')) {
				return false;
			}
			var dd = othis.is('dd') ? othis : othis.parents('dd');
			var id = dd.parent('dl').attr('xid');
			if (dd.hasClass(DISABLED)) {
				//被禁用选项的处理
				return false;
			}
			if (dd.hasClass(FORM_SELECT_TIPS)) {
				//tips的处理
				var btn = othis.is('.' + CZ) ? othis : othis.parents('.' + CZ);
				if (!btn[0]) {
					return false;
				}
				//TODO 快捷操作
				var method = btn.attr('method');
				var obj = data[id].config.btns.filter(function (bean) {
					return bean.name == method;
				})[0];
				obj && obj.click && obj.click instanceof Function && obj.click(id, _this8);
				return false;
			}
			var isAdd = !dd.hasClass(THIS);
			_this8.handlerLabel(id, dd, isAdd);
			return false;
		});
	};

	Common.prototype.linkageAdd = function (id, val) {
		var dl = $('dl[xid="' + id + '"]');
		dl.find('.xm-select-active').removeClass('xm-select-active');
		var vs = val.val.split('/');
		var pid = void 0,
		    li = void 0,
		    index = 0;
		var lis = [];
		do {
			pid = vs[index];
			li = dl.find('.xm-select-linkage-group' + (index + 1) + ' li[value="' + pid + '"]');
			li[0] && lis.push(li);
			index++;
		} while (li.length && pid != undefined);
		if (lis.length == vs.length) {
			$.each(lis, function (idx, item) {
				item.addClass('xm-select-this');
			});
		}
	};

	Common.prototype.linkageDel = function (id, val) {
		var dl = $('dl[xid="' + id + '"]');
		var vs = val.val.split('/');
		var pid = void 0,
		    li = void 0,
		    index = vs.length - 1;
		do {
			pid = vs[index];
			li = dl.find('.xm-select-linkage-group' + (index + 1) + ' li[value="' + pid + '"]');
			if (!li.parent().next().find('li[pid=' + pid + '].xm-select-this').length) {
				li.removeClass('xm-select-this');
			}
			index--;
		} while (li.length && pid != undefined);
	};

	Common.prototype.valToName = function (id, val) {
		var dl = $('dl[xid="' + id + '"]');
		var vs = (val + "").split('/');
		var names = [];
		$.each(vs, function (idx, item) {
			var name = dl.find('.xm-select-linkage-group' + (idx + 1) + ' li[value="' + item + '"] span').text();
			names.push(name);
		});
		return names.length == vs.length ? names.join('/') : null;
	};

	Common.prototype.commonHanler = function (key, label) {
		//计算input的提示语
		this.changePlaceHolder(label);
		//计算高度
		this.retop(label.parents('.' + FORM_SELECT));
		this.checkHideSpan(label);
		this.calcLeft(key, label);
		//表单默认值
		label.parents('.' + PNAME).find('.' + HIDE_INPUT).val(data[key].values.map(function (val) {
			return val.val;
		}).join(','));
		//title值
		label.parents('.' + FORM_TITLE + ' .' + NAME).attr('title', data[key].values.map(function (val) {
			return val.name;
		}).join(','));
	};

	Common.prototype.initVal = function (id) {
		var _this9 = this;

		var target = {};
		if (id) {
			target[id] = data[id];
		} else {
			target = data;
		}
		$.each(target, function (key, val) {
			var values = val.values,
			    div = $('dl[xid="' + key + '"]').parent(),
			    label = div.find('.' + LABEL),
			    dl = div.find('dl');
			dl.find('dd.' + THIS).removeClass(THIS);

			var _vals = values.concat([]);
			_vals.concat([]).forEach(function (item, index) {
				_this9.addLabel(key, label, item);
				dl.find('dd[lay-value="' + item.val + '"]').addClass(THIS);
			});
			if (val.config.radio) {
				_vals.length && values.push(_vals[_vals.length - 1]);
			}
			_this9.commonHanler(key, label);
		});
	};

	Common.prototype.handlerLabel = function (id, dd, isAdd, oval, notOn) {
		var div = $('[xid="' + id + '"]').prev().find('.' + LABEL),
		    val = dd && {
			name: dd.find('span').text(),
			val: dd.attr('lay-value')
		},
		    vals = data[id].values,
		    on = data[id].config.on || events.on[id];
		if (oval) {
			val = oval;
		}
		var fs = data[id];
		if (isAdd && fs.config.max && fs.values.length >= fs.config.max) {
			var maxTipsFun = data[id].config.maxTips || events.maxTips[id];
			maxTipsFun && maxTipsFun(id, vals.concat([]), val, fs.max);
			return;
		}
		if (!notOn) {
			if (on && on instanceof Function && on(id, vals.concat([]), val, isAdd, dd && dd.hasClass(DISABLED) == false)) {
				return;
			}
		}
		var dl = $('dl[xid="' + id + '"]');
		isAdd ? (dd && dd[0] ? (dd.addClass(THIS), dd.removeClass(TEMP)) : dl.find('.xm-select-linkage')[0] && this.linkageAdd(id, val), this.addLabel(id, div, val), vals.push(val)) : (dd && dd[0] ? dd.removeClass(THIS) : dl.find('.xm-select-linkage')[0] && this.linkageDel(id, val), this.delLabel(id, div, val), this.remove(vals, val));
		if (!div[0]) return;
		//单选选完后直接关闭选择域
		if (fs.config.radio) {
			this.changeShow(div, false);
		}
		//移除表单验证的红色边框
		div.parents('.' + FORM_TITLE).prev().removeClass('layui-form-danger');

		//清空搜索值
		fs.config.clearInput && div.parents('.' + PNAME).find('.' + INPUT).val('');

		this.commonHanler(id, div);
	};

	Common.prototype.addLabel = function (id, div, val) {
		if (!val) return;
		var tips = 'fsw="' + NAME + '"';
		var _ref = [$('<span ' + tips + ' value="' + val.val + '"><font ' + tips + '>' + val.name + '</font></span>'), $('<i ' + tips + ' class="xm-icon-close">\xD7</i>')],
		    $label = _ref[0],
		    $close = _ref[1];

		$label.append($close);
		//如果是radio模式
		var fs = data[id];
		if (fs.config.radio) {
			fs.values.length = 0;
			$('dl[xid="' + id + '"]').find('dd.' + THIS + ':not([lay-value="' + val.val + '"])').removeClass(THIS);
			div.find('span').remove();
		}
		//如果是固定高度
		if (fs.config.height) {
			div.append($label);
		} else {
			div.find('input').css('width', '50px');
			div.find('input').before($label);
		}
	};

	Common.prototype.delLabel = function (id, div, val) {
		if (!val) return;
		div.find('span[value="' + val.val + '"]:first').remove();
	};

	Common.prototype.calcLeft = function (id, div) {
		if (data[id].config.height) {
			var showLastSpan = div.find('span:not(.xm-span-hide):last')[0];
			div.next().css('left', (showLastSpan ? this.getPosition(showLastSpan).x - this.getPosition(div[0]).x + showLastSpan.offsetWidth + 20 : 10) + 'px');
		}
	};

	Common.prototype.checkHideSpan = function (div) {
		var _this10 = this;

		var parentHeight = div.parents('.' + NAME)[0].offsetHeight + 5;
		div.find('span.xm-span-hide').removeClass('xm-span-hide');
		div.find('span').each(function (index, item) {
			if (item.offsetHeight + item.offsetTop > parentHeight || _this10.getPosition(item).y + item.offsetHeight > _this10.getPosition(div[0]).y + div[0].offsetHeight + 5) {
				$(item).addClass('xm-span-hide');
			}
		});
	};

	Common.prototype.retop = function (div) {
		//计算dl显示的位置
		var dl = div.find('dl'),
		    top = div.offset().top + div.outerHeight() + 5 - $win.scrollTop(),
		    dlHeight = dl.outerHeight();
		var up = div.hasClass('layui-form-selectup') || dl.css('top').indexOf('-') != -1 || top + dlHeight > $win.height() && top >= dlHeight;
		div = div.find('.' + NAME);

		var fs = data[dl.attr('xid')];
		var base = dl.parents('.layui-form-pane')[0] && dl.prev()[0].clientHeight > 38 ? 14 : 10;
		if (fs) {
			if (fs.config.direction == 'up') {
				dl.css({
					top: 'auto',
					bottom: '42px'
				});
				return;
			}
			if (fs.direction == 'down') {
				dl.css({
					top: div[0].offsetTop + div.height() + base + 'px',
					bottom: 'auto'
				});
				return;
			}
		}

		if (up) {
			dl.css({
				top: 'auto',
				bottom: '42px'
			});
		} else {
			dl.css({
				top: div[0].offsetTop + div.height() + base + 'px',
				bottom: 'auto'
			});
		}
	};

	Common.prototype.changeShow = function (children, isShow) {
		//显示于隐藏
		var top = children.parents('.' + FORM_SELECT);
		$('.' + PNAME + ' .' + FORM_SELECT).not(top).removeClass(FORM_SELECTED);
		if (isShow) {
			this.retop(top);
			top.addClass(FORM_SELECTED);
			top.find('.' + INPUT).focus();
		} else {
			top.removeClass(FORM_SELECTED);
			top.find('.' + INPUT).val('');
			top.find('dl .layui-hide').removeClass('layui-hide');
			top.find('dl dd.' + TEMP).remove();
			//计算ajax数据是否为空, 然后重新请求数据
			var id = top.find('dl').attr('xid');
			if (id && data[id] && data[id].config.isEmpty) {
				this.triggerSearch(top);
			}
		}
	};

	Common.prototype.changePlaceHolder = function (div) {
		//显示于隐藏提示语
		//调整pane模式下的高度
		var title = div.parents('.' + FORM_TITLE);

		var id = div.parents('.' + PNAME).find('dl[xid]').attr('xid');
		if (data[id] && data[id].config.height) {//既然固定高度了, 那就看着办吧

		} else {
			var height = title.find('.' + NAME)[0].clientHeight;
			title.css('height', (height > 34 ? height + 4 : height) + 'px');
			//如果是layui pane模式, 处理label的高度
			var label = title.parents('.' + PNAME).parent().prev();
			if (label.is('.layui-form-label') && title.parents('.layui-form-pane')[0]) {
				height = height > 36 ? height + 4 : height;
				title.css('height', height + 'px');
				label.css({
					height: height + 2 + 'px',
					lineHeight: height - 18 + 'px'
				});
			}
		}

		var input = title.find('.' + TDIV + ' input'),
		    isShow = !div.find('span:last')[0] && !title.find('.' + INPUT).val();
		if (isShow) {
			var ph = input.attr('back');
			input.removeAttr('back');
			input.attr('placeholder', ph);
		} else {
			var _ph = input.attr('placeholder');
			input.removeAttr('placeholder');
			input.attr('back', _ph);
		}
	};

	Common.prototype.indexOf = function (arr, val) {
		for (var i = 0; i < arr.length; i++) {
			if (arr[i].val == val || arr[i].val == (val ? val.val : val) || arr[i] == val || JSON.stringify(arr[i]) == JSON.stringify(val)) {
				return i;
			}
		}
		return -1;
	};

	Common.prototype.remove = function (arr, val) {
		var idx = this.indexOf(arr, val ? val.val : val);
		if (idx > -1) {
			arr.splice(idx, 1);
			return true;
		}
		return false;
	};

	Common.prototype.selectAll = function (id, isOn, skipDis) {
		var _this11 = this;

		var dl = $('[xid="' + id + '"]');
		if (dl.find('.xm-select-linkage')[0]) {
			return;
		}
		dl.find('dd[lay-value]:not(.' + FORM_SELECT_TIPS + '):not(.' + THIS + ')' + (skipDis ? ':not(.' + DISABLED + ')' : '')).each(function (index, item) {
			item = $(item);
			var val = {
				name: item.find('span').text(),
				val: item.attr('lay-value')
			};
			_this11.handlerLabel(id, dl.find('dd[lay-value="' + val.val + '"]'), true, val, !isOn);
		});
	};

	Common.prototype.removeAll = function (id, isOn, skipDis) {
		var _this12 = this;

		var dl = $('[xid="' + id + '"]');
		if (dl.find('.xm-select-linkage')[0]) {
			//针对多级联动的处理
			data[id].values.concat([]).forEach(function (item, idx) {
				var vs = item.val.split('/');
				var pid = void 0,
				    li = void 0,
				    index = 0;
				do {
					pid = vs[index++];
					li = dl.find('.xm-select-linkage-group' + index + ':not(.xm-select-linkage-hide) li[value="' + pid + '"]');
					li.click();
				} while (li.length && pid != undefined);
			});
			return;
		}
		data[id].values.concat([]).forEach(function (item, index) {
			if (skipDis && dl.find('dd[lay-value="' + item.val + '"]').hasClass(DISABLED)) {} else {
				_this12.handlerLabel(id, dl.find('dd[lay-value="' + item.val + '"]'), false, item, !isOn);
			}
		});
	};

	Common.prototype.reverse = function (id, isOn, skipDis) {
		var _this13 = this;

		var dl = $('[xid="' + id + '"]');
		if (dl.find('.xm-select-linkage')[0]) {
			return;
		}
		dl.find('dd[lay-value]:not(.' + FORM_SELECT_TIPS + ')' + (skipDis ? ':not(.' + DISABLED + ')' : '')).each(function (index, item) {
			item = $(item);
			var val = {
				name: item.find('span').text(),
				val: item.attr('lay-value')
			};
			_this13.handlerLabel(id, dl.find('dd[lay-value="' + val.val + '"]'), !item.hasClass(THIS), val, !isOn);
		});
	};

	Common.prototype.skin = function (id) {
		var skins = ['default', 'primary', 'normal', 'warm', 'danger'];
		var skin = skins[Math.floor(Math.random() * skins.length)];
		$('dl[xid="' + id + '"]').parents('.' + PNAME).find('.' + FORM_SELECT).attr('xm-select-skin', skin);
		this.commonHanler(id, $('dl[xid="' + id + '"]').parents('.' + PNAME).find('.' + LABEL));
	};

	Common.prototype.getPosition = function (e) {
		var x = 0,
		    y = 0;
		while (e != null) {
			x += e.offsetLeft;
			y += e.offsetTop;
			e = e.offsetParent;
		}
		return { x: x, y: y };
	};

	Common.prototype.onreset = function () {
		//监听reset按钮, 然后重置多选
		$(document).on('click', '[type=reset]', function (e) {
			$(e.target).parents('form').find('.' + PNAME + ' dl[xid]').each(function (index, item) {
				var id = item.getAttribute('xid'),
				    dl = $(item),
				    dd = void 0,
				    temp = {};
				common.removeAll(id);
				data[id].config.init.forEach(function (val, idx) {
					if (val && (!temp[val] || data[id].config.repeat) && (dd = dl.find('dd[lay-value="' + val.val + '"]'))[0]) {
						common.handlerLabel(id, dd, true);
						temp[val] = 1;
					}
				});
			});
		});
	};

	Common.prototype.loadingCss = function () {};

	Common.prototype.listening = function () {
		//TODO 用于监听dom结构变化, 如果出现新的为渲染select, 则自动进行渲染
		var flag = false;
		var index = 0;
		$(document).on('DOMNodeInserted', function (e) {
			if (flag) {
				//避免递归渲染
				return;
			}
			flag = true;
			//渲染select
			$('select[' + NAME + ']').each(function (index, select) {
				var sid = select.getAttribute(NAME);
				common.init(select);
				common.one($('dl[xid="' + sid + '"]').parents('.' + PNAME));
				common.initVal(sid);
			});

			flag = false;
		});
	};

	var Select4 = function Select4() {
		this.v = v;
	};
	var common = new Common();

	Select4.prototype.value = function (id, type, isAppend) {
		if (typeof id != 'string') {
			return [];
		}
		var fs = data[id];
		if (!fs) {
			return [];
		}
		if (typeof type == 'string' || type == undefined) {
			var arr = fs.values.concat([]) || [];
			if (type == 'val') {
				return arr.map(function (val) {
					return val.val;
				});
			}
			if (type == 'valStr') {
				return arr.map(function (val) {
					return val.val;
				}).join(',');
			}
			if (type == 'name') {
				return arr.map(function (val) {
					return val.name;
				});
			}
			if (type == 'nameStr') {
				return arr.map(function (val) {
					return val.name;
				}).join(',');
			}
			return arr;
		}
		if (common.isArray(type)) {
			var dl = $('[xid="' + id + '"]'),
			    temp = {},
			    dd = void 0,
			    isAdd = true;
			if (isAppend == false) {
				//删除传入的数组
				isAdd = false;
			} else if (isAppend == true) {
				//追加模式
				isAdd = true;
			} else {
				//删除原有的数据
				common.removeAll(id);
			}
			if (isAdd) {
				fs.values.forEach(function (val, index) {
					temp[val.val] = 1;
				});
			}
			type.forEach(function (val, index) {
				if (val && (!temp[val] || fs.config.repeat)) {
					if ((dd = dl.find('dd[lay-value="' + val + '"]'))[0]) {
						common.handlerLabel(id, dd, isAdd, null, true);
						temp[val] = 1;
					} else {
						var name = common.valToName(id, val);
						if (name) {
							common.handlerLabel(id, dd, isAdd, {
								name: name,
								val: val
							}, true);
							temp[val] = 1;
						}
					}
				}
			});
		}
	};

	Common.prototype.bindEvent = function (name, id, fun) {
		if (id && id instanceof Function) {
			fun = id;
			id = null;
		}
		if (fun && fun instanceof Function) {
			if (!id) {
				$.each(data, function (id, val) {
					data[id] ? data[id].config[name] = fun : events[name][id] = fun;
				});
			} else {
				data[id] ? data[id].config[name] = fun : events[name][id] = fun;
			}
		}
	};

	Select4.prototype.on = function (id, fun) {
		common.bindEvent('on', id, fun);
		return this;
	};

	Select4.prototype.filter = function (id, fun) {
		common.bindEvent('filter', id, fun);
		return this;
	};

	Select4.prototype.maxTips = function (id, fun) {
		common.bindEvent('maxTips', id, fun);
		return this;
	};

	Select4.prototype.config = function (id, config, isJson) {
		if (id && (typeof id === 'undefined' ? 'undefined' : _typeof(id)) == 'object') {
			isJson = config == true;
			config = id;
			id = null;
		}
		if (config && (typeof config === 'undefined' ? 'undefined' : _typeof(config)) == 'object') {
			if (isJson) {
				config.header || (config.header = {});
				config.header['Content-Type'] = 'application/json; charset=UTF-8';
				config.dataType = 'json';
			}
			id ? (ajaxs[id] = $.extend(true, {}, ajax, config), data[id] && (data[id].config.direction = config.direction), config.searchUrl && data[id] && common.triggerSearch($('.' + PNAME + ' dl[xid="' + id + '"]').parents('.' + FORM_SELECT), true)) : $.extend(true, ajax, config);
		}
		return this;
	};

	Select4.prototype.render = function (id) {
		var _this14 = this;

		var target = {};
		id ? data[id] && (target[id] = data[id]) : data;

		if (Object.getOwnPropertyNames(target).length) {
			$.each(target, function (key, val) {
				//恢复初始值
				var dl = $('dl[xid="' + key + '"]'),
				    vals = [];
				val.select.find('option[selected]').each(function (index, item) {
					vals.push(item.value);
				});
				//移除创建元素
				dl.find('.' + CREATE_LONG).remove();
				//清空INPUT
				dl.prev().find('.' + INPUT).val('');
				//触发search
				common.triggerSearch(dl.parents('.' + FORM_SELECT), true);
				//移除hidn
				dl.find('.layui-hide').removeClass('layui-hide');
				//重新赋值
				_this14.value(key, vals);
			});
		}
		($('select[' + NAME + '="' + id + '"]')[0] ? $('select[' + NAME + '="' + id + '"]') : $('select[' + NAME + ']')).each(function (index, select) {
			var sid = select.getAttribute(NAME);
			common.init(select);
			common.one($('dl[xid="' + sid + '"]').parents('.' + PNAME));
			common.initVal(sid);
		});
		return this;
	};

	Select4.prototype.disabled = function (id) {
		var target = {};
		id ? data[id] && (target[id] = data[id]) : target = data;

		$.each(target, function (key, val) {
			$('dl[xid="' + key + '"]').prev().addClass(DIS);
		});
		return this;
	};

	Select4.prototype.undisabled = function (id) {
		var target = {};
		id ? data[id] && (target[id] = data[id]) : target = data;

		$.each(target, function (key, val) {
			$('dl[xid="' + key + '"]').prev().removeClass(DIS);
		});
		return this;
	};

	Select4.prototype.data = function (id, type, config) {
		if (!id || !type || !config) {
			return this;
		}
		//检测该id是否尚未渲染
		!data[id] && this.render(id).value(id, []);
		this.config(id, config);
		if (type == 'local') {
			common.renderData(id, config.arr, config.linkage == true, config.linkageWidth ? config.linkageWidth : '100');
		} else if (type == 'server') {
			common.ajax(id, config.url, config.keyword, config.linkage == true, config.linkageWidth ? config.linkageWidth : '100');
		}
		return this;
	};

	Select4.prototype.btns = function (id, btns, config) {
		if (!btns || !common.isArray(btns)) {
			return this;
		};
		var target = {};
		id ? data[id] && (target[id] = data[id]) : target = data;

		btns = btns.map(function (obj) {
			if (typeof obj == 'string') {
				if (obj == 'select') {
					return quickBtns[0];
				}
				if (obj == 'remove') {
					return quickBtns[1];
				}
				if (obj == 'reverse') {
					return quickBtns[2];
				}
				if (obj == 'skin') {
					return quickBtns[3];
				}
			}
			return obj;
		});

		$.each(target, function (key, val) {
			val.config.btns = btns;
			var dd = $('dl[xid="' + key + '"]').find('.' + FORM_SELECT_TIPS + ':first');
			if (btns.length) {
				var show = config && config.show && (config.show == 'name' || config.show == 'icon') ? config.show : '';
				var html = common.renderBtns(key, show, config && config.space ? config.space : '30px');
				dd.html(html);
			} else {
				var pcInput = dd.parents('.' + FORM_SELECT).find('.' + TDIV + ' input');
				var _html = pcInput.attr('placeholder') || pcInput.attr('back');
				dd.html(_html);
				dd.removeAttr('style');
			}
		});

		return this;
	};

	Select4.prototype.search = function (id, val) {
		if (id && data[id]) {
			ajaxs[id] = $.extend(true, {}, ajax, {
				first: true,
				searchVal: val
			});
			common.triggerSearch($('dl[xid="' + id + '"]').parents('.' + FORM_SELECT), true);
		}
		return this;
	};

	return new Select4();
});