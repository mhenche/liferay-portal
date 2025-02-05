;(function(A, $, _, Liferay) {
	A.use('aui-base-lang');

	var Lang = A.Lang;

	var AArray = A.Array;
	var AString = A.Lang.String;
	var Browser = Liferay.Browser;

	var isArray = Lang.isArray;
	var arrayIndexOf = AArray.indexOf;
	var prefix = AString.prefix;
	var startsWith = AString.startsWith;

	var EVENT_CLICK = 'click';

	var STR_RIGHT_SQUARE_BRACKET = ']';

	var SRC_HIDE_LINK = {
		src: 'hideLink'
	};

	var STR_CHECKED = 'checked';

	var REGEX_PORTLET_ID = /^(?:p_p_id)?_(.*)_.*$/;

	var Window = {
		_map: {}
	};

	var Util = {
		submitCountdown: 0,

		addInputCancel: function() {
			A.use(
				'aui-button-search-cancel',
				function(A) {
					new A.ButtonSearchCancel(
						{
							trigger: 'input[type=password], input[type=search], input.clearable, input.search-query'
						}
					);
				}
			);

			Util.addInputCancel = function(){};
		},

		addParams: function(params, url) {
			A.use('querystring-stringify-simple');

			if (Lang.isObject(params)) {
				params = A.QueryString.stringify(params);
			}
			else {
				params = Lang.trim(params);
			}

			if (params) {
				var loc = url || location.href;

				var anchorHash;
				var finalUrl;

				if (loc.indexOf('#') > -1) {
					var locationPieces = loc.split('#');
					loc = locationPieces[0];
					anchorHash = locationPieces[1];
				}

				if (loc.indexOf('?') == -1) {
					params = '?' + params;
				}
				else {
					params = '&' + params;
				}

				if (loc.indexOf(params) == -1) {
					finalUrl = loc + params;

					if (anchorHash) {
						finalUrl += '#' + anchorHash;
					}
					if (!url) {
						location.href = finalUrl;
					}

					return finalUrl;
				}
			}
		},

		checkTab: function(box) {
			if ((document.all) && (event.keyCode == 9)) {
				box.selection = document.selection.createRange();

				setTimeout(
					function() {
						Util.processTab(box.id);
					},
					0
				);
			}
		},

		disableEsc: function() {
			if ((document.all) && (event.keyCode == 27)) {
				event.returnValue = false;
			}
		},

		disableFormButtons: function(inputs, form) {
			inputs.attr('disabled', true);
			inputs.setStyle('opacity', 0.5);

			if (A.UA.gecko) {
				A.getWin().on(
					'unload',
					function(event) {
						inputs.attr('disabled', false);
					}
				);
			}
		},

		enableFormButtons: function(inputs, form) {
			Util._submitLocked = null;

			document.body.style.cursor = 'auto';

			inputs.attr('disabled', false);
			inputs.setStyle('opacity', 1);
		},

		escapeCDATA: function(str) {
			return str.replace(
				/<!\[CDATA\[|\]\]>/gi,
				function(match) {
					var str = '';

					if (match == ']]>') {
						str = ']]&gt;';
					}
					else if (match == '<![CDATA[') {
						str = '&lt;![CDATA[';
					}

					return str;
				}
			);
		},

		getAttributes: function(el, attributeGetter) {
			var instance = this;

			var result = null;

			if (el) {
				if (Lang.isFunction(el.getDOM)) {
					el = el.getDOM();
				}

				result = {};

				var isGetterFn = Lang.isFunction(attributeGetter);
				var isGetterString = Lang.isString(attributeGetter);

				var attrs = el.attributes;
				var length = attrs.length;

				while (length--) {
					var attr = attrs[length];
					var name = attr.nodeName.toLowerCase();
					var value = attr.nodeValue;

					if (isGetterString) {
						if (name.indexOf(attributeGetter) === 0) {
							name = name.substr(attributeGetter.length);
						}
						else {
							continue;
						}
					}
					else if (isGetterFn) {
						value = attributeGetter(value, name, attrs);

						if (value === false) {
							continue;
						}
					}

					result[name] = value;
				}
			}

			return result;
		},

		getColumnId: function(str) {
			var columnId = str.replace(/layout-column_/, '');

			return columnId;
		},

		getGeolocation: function(success, fallback, options) {
			if (success && navigator.geolocation) {
				navigator.geolocation.getCurrentPosition(
					function(position) {
						success(
							position.coords.latitude,
							position.coords.longitude,
							position
						);
					},
					fallback,
					options
				);
			}
			else if (fallback) {
				fallback();
			}
		},

		getOpener: function() {
			var openingWindow = Window._opener;

			if (!openingWindow) {
				var topUtil = Liferay.Util.getTop().Liferay.Util;

				var windowName = Liferay.Util.getWindowName();

				var dialog = topUtil.Window._map[windowName];

				if (dialog) {
					openingWindow = topUtil.Window._map[windowName]._opener;

					Window._opener = openingWindow;
				}
			}

			return openingWindow || window.opener || window.parent;
		},

		getPortletId: function(portletId) {
			return String(portletId).replace(REGEX_PORTLET_ID, '$1');
		},

		getPortletNamespace: function(portletId) {
			return '_' + portletId + '_';
		},

		getTop: function() {
			var topWindow = Util._topWindow;

			if (!topWindow) {
				var parentWindow = window.parent;

				var parentThemeDisplay;

				while (parentWindow != window) {
					try {
						if (typeof parentWindow.location.href == 'undefined') {
							break;
						}
					}
					catch (e) {
						break;
					}

					parentThemeDisplay = parentWindow.themeDisplay;

					if (!parentThemeDisplay || window.name === 'devicePreviewIframe') {
						break;
					}
					else if (!parentThemeDisplay.isStatePopUp() || (parentWindow == parentWindow.parent)) {
						topWindow = parentWindow;

						break;
					}

					parentWindow = parentWindow.parent;
				}

				if (!topWindow) {
					topWindow = window;
				}

				Util._topWindow = topWindow;
			}

			return topWindow;
		},

		getURLWithSessionId: function(url) {
			if (!themeDisplay.isAddSessionIdToURL()) {
				return url;
			}

			// LEP-4787

			var x = url.indexOf(';');

			if (x > -1) {
				return url;
			}

			var sessionId = ';jsessionid=' + themeDisplay.getSessionId();

			x = url.indexOf('?');

			if (x > -1) {
				return url.substring(0, x) + sessionId + url.substring(x);
			}

			// In IE6, http://www.abc.com;jsessionid=XYZ does not work, but
			// http://www.abc.com/;jsessionid=XYZ does work.

			x = url.indexOf('//');

			if (x > -1) {
				var y = url.lastIndexOf('/');

				if (x + 1 == y) {
					return url + '/' + sessionId;
				}
			}

			return url + sessionId;
		},

		getWindow: function(id) {
			if (!id) {
				id = Util.getWindowName();
			}

			return Util.getTop().Liferay.Util.Window.getById(id);
		},

		getWindowName: function() {
			return window.name || Window._name || '';
		},

		getWindowWidth: function() {
			return (window.innerWidth > 0) ? window.innerWidth : screen.width;
		},

		inBrowserView: function(node, win, nodeRegion) {
			var DOM = A.DOM;

			var viewable = false;

			node = A.one(node);

			if (node) {
				if (!nodeRegion) {
					nodeRegion = node.get('region');
				}

				if (!win) {
					win = window;
				}

				var winRegion = DOM.viewportRegion(win);

				var viewable = (
					nodeRegion.bottom <= winRegion.bottom &&
					nodeRegion.left >= winRegion.left &&
					nodeRegion.right <= winRegion.right &&
					nodeRegion.top >= winRegion.top
				);

				if (viewable) {
					var frameEl = win.frameElement;

					if (frameEl) {
						var frameXY = DOM.getXY(frameEl);

						var xOffset = frameXY[0] - DOM.docScrollX(win);

						nodeRegion.left += xOffset;
						nodeRegion.right += xOffset;

						var yOffset = frameXY[1] - DOM.docScrollY(win);

						nodeRegion.top += yOffset;
						nodeRegion.bottom += yOffset;

						viewable = Util.inBrowserView(node, win.parent, nodeRegion);
					}
				}
			}

			return viewable;
		},

		isPhone: function() {
			var instance = this;

			return (instance.getWindowWidth() < Liferay.BREAKPOINTS.PHONE);
		},

		isTablet: function() {
			var instance = this;

			return (instance.getWindowWidth() < Liferay.BREAKPOINTS.TABLET);
		},

		listCheckboxesExcept: function(form, except, name, checked) {
			if (form._node) {
				form = form.getDOM();
			}

			var selector = 'input[type=checkbox]';

			if (name) {
				selector += '[name=' + name + ']';
			}

			return _.reduce(
				$(form).find(selector),
				function(prev, item, index) {
					item = $(item);

					var val = item.val();

					if (val && (item.attr('name') != except) && (item.prop('checked') == checked) && !item.prop('disabled')) {
						prev.push(val);
					}

					return prev;
				},
				[]
			).join();
		},

		listCheckedExcept: function(form, except, name) {
			return Util.listCheckboxesExcept(form, except, name, true);
		},

		listSelect: function(select, delimeter) {
			if (select._node) {
				select = select.getDOM();
			}

			return _.reduce(
				$(select).find('option'),
				function(prev, item, index) {
					var val = $(item).val();

					if (val) {
						prev.push(val);
					}

					return prev;
				},
				[]
			).join(delimeter || ',');
		},

		listUncheckedExcept: function(form, except, name) {
			return Util.listCheckboxesExcept(form, except, name, false);
		},

		ns: function(namespace, obj) {
			var instance = this;

			var value;

			var ns = instance._ns;

			if (!Lang.isObject(obj)) {
				value = ns(namespace, obj);
			}
			else {
				value = {};

				A.Object.each(
					obj,
					function(item, index) {
						index = ns(namespace, index);

						value[index] = item;
					}
				);
			}

			return value;
		},

		openInDialog: function(event) {
			event.preventDefault();

			var currentTarget = event.currentTarget;

			var config = currentTarget.getData();

			if (!config.uri) {
				config.uri = currentTarget.getData('href') || currentTarget.attr('href');
			}

			if (!config.title) {
				config.title = currentTarget.attr('title');
			}

			Liferay.Util.openWindow(config);
		},

		openWindow: function(config, callback) {
			config.openingWindow = window;

			var top = Util.getTop();

			var topUtil = top.Liferay.Util;

			topUtil._openWindowProvider(config, callback);
		},

		processTab: function(id) {
			document.all[id].selection.text = String.fromCharCode(9);
			document.all[id].focus();
		},

		randomInt: function() {
			return (Math.ceil(Math.random() * (new Date()).getTime()));
		},

		selectAndCopy: function(el) {
			el.focus();
			el.select();

			if (document.all) {
				var textRange = el.createTextRange();

				textRange.execCommand('copy');
			}
		},

		setCursorPosition: function(el, position) {
			var instance = this;

			instance.setSelectionRange(el, position, position);
		},

		setSelectionRange: function(el, selectionStart, selectionEnd) {
			var instance = this;

			if (Lang.isFunction(el.getDOM)) {
				el = el.getDOM();
			}

			if (el.setSelectionRange) {
				el.focus();

				el.setSelectionRange(selectionStart, selectionEnd);
			}
			else if (el.createTextRange) {
				var textRange = el.createTextRange();

				textRange.collapse(true);

				textRange.moveEnd('character', selectionEnd);
				textRange.moveEnd('character', selectionStart);

				textRange.select();
			}
		},

		showCapsLock: function(event, span) {
			var keyCode = event.keyCode ? event.keyCode : event.which;
			var shiftKey = event.shiftKey ? event.shiftKey : ((keyCode == 16) ? true : false);

			if (((keyCode >= 65 && keyCode <= 90) && !shiftKey) ||
				((keyCode >= 97 && keyCode <= 122) && shiftKey)) {

				document.getElementById(span).style.display = '';
			}
			else {
				document.getElementById(span).style.display = 'none';
			}
		},

		sortByAscending: function(a, b) {
			a = a[1].toLowerCase();
			b = b[1].toLowerCase();

			if (a > b) {
				return 1;
			}

			if (a < b) {
				return -1;
			}

			return 0;
		},

		toCharCode: A.cached(
			function(name) {
				var buffer = [];

				for (var i = 0; i < name.length; i++) {
					buffer[i] = name.charCodeAt(i);
				}

				return buffer.join('');
			}
		),

		toNumber: function(value) {
			return parseInt(value, 10) || 0;
		},

		_defaultPreviewArticleFn: function(event) {
			var instance = this;

			event.preventDefault();

			Liferay.Util.openWindow(
				{
					cache: false,
					title: event.title,
					uri: event.uri
				}
			);
		},

		_defaultSubmitFormFn: function(event) {
			var form = event.form;

			var hasErrors = false;

			if (event.validate) {
				var liferayForm = Liferay.Form.get(form.attr('id'));

				if (liferayForm) {
					var validator = liferayForm.formValidator;

					if (A.instanceOf(validator, A.FormValidator)) {
						validator.validate();

						hasErrors = validator.hasErrors();

						if (hasErrors) {
							validator.focusInvalidField();
						}
					}
				}
			}

			if (!hasErrors) {
				var action = event.action;
				var singleSubmit = event.singleSubmit;

				var inputs = form.all('input[type=button], input[type=image], input[type=reset], input[type=submit]');

				Util.disableFormButtons(inputs, form);

				if (singleSubmit === false) {
					Util._submitLocked = A.later(
						1000,
						Util,
						Util.enableFormButtons,
						[inputs, form]
					);
				}
				else {
					Util._submitLocked = true;
				}

				if (action !== null) {
					form.attr('action', action);
				}

				form.submit();

				form.attr('target', '');
			}
		},

		_getEditableInstance: function(title) {
			var editable = Util._EDITABLE;

			if (!editable) {
				editable = new A.Editable(
					{
						after: {
							contentTextChange: function(event) {
								var instance = this;

								if (!event.initial) {
									var title = instance.get('node');

									var portletTitleEditOptions = title.getData('portletTitleEditOptions');

									Util.savePortletTitle(
										{
											doAsUserId: portletTitleEditOptions.doAsUserId,
											plid: portletTitleEditOptions.plid,
											portletId: portletTitleEditOptions.portletId,
											title: event.newVal
										}
									);
								}
							},
							startEditing: function(event) {
								var instance = this;

								var Layout = Liferay.Layout;

								if (Layout) {
									instance._dragListener = Layout.getLayoutHandler().on(
										'drag:start',
										function(event) {
											instance.fire('save');
										}
									);
								}
							},
							stopEditing: function(event) {
								var instance = this;

								if (instance._dragListener) {
									instance._dragListener.detach();
								}
							}
						},
						cssClass: 'lfr-portlet-title-editable',
						node: title
					}
				);

				Util._EDITABLE = editable;
			}

			return editable;
		},

		_ns: A.cached(
			function(namespace, str) {
				var value = str;

				if (!Lang.isUndefined(str) && !startsWith(str, namespace)) {
					value = prefix(namespace, str);
				}

				return value;
			}
		)
	};

	Liferay.provide(
		Util,
		'afterIframeLoaded',
		function(event) {
			var nodeInstances = A.Node._instances;

			var docEl = event.doc;

			var docUID = docEl._yuid;

			if (docUID in nodeInstances) {
				delete nodeInstances[docUID];
			}

			var iframeDocument = A.one(docEl);

			var iframeBody = iframeDocument.one('body');

			var dialog = event.dialog;

			iframeBody.addClass('dialog-iframe-popup');

			var detachEventHandles = function() {
				AArray.invoke(eventHandles, 'detach');

				iframeDocument.purge(true);
			};

			var eventHandles = [
				iframeBody.delegate('submit', detachEventHandles, 'form'),

				iframeBody.delegate(
					EVENT_CLICK,
					function() {
						dialog.set('visible', false, SRC_HIDE_LINK);

						detachEventHandles();
					},
					'.lfr-hide-dialog'
				)
			];

			var cancelButton = iframeBody.one('.btn-cancel');

			if (cancelButton) {
				cancelButton.after(
					EVENT_CLICK,
					function() {
						detachEventHandles();

						dialog.hide();
					}
				);
			}

			var rolesSearchContainer = iframeBody.one('#rolesSearchContainerSearchContainer');

			if (rolesSearchContainer) {
				eventHandles.push(
					rolesSearchContainer.delegate(
						EVENT_CLICK,
						function(event) {
							event.preventDefault();

							detachEventHandles();

							submitForm(document.hrefFm, event.currentTarget.attr('href'));
						},
						'a'
					)
				);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'check',
		function(form, name, checked) {
			var checkbox = A.one(form[name]);

			if (checkbox) {
				checkbox.attr(STR_CHECKED, checked);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'checkAll',
		function(form, name, allBox, selectClassName) {
			var selector;

			if (isArray(name)) {
				selector = 'input[name=' + name.join('], input[name=') + STR_RIGHT_SQUARE_BRACKET;
			}
			else {
				selector = 'input[name=' + name + STR_RIGHT_SQUARE_BRACKET;
			}

			form = A.one(form);

			var allBoxChecked = A.one(allBox).get(STR_CHECKED);

			form.all(selector).each(
				function(item, index) {
					if (!item.attr('disabled')) {
						item.attr(STR_CHECKED, allBoxChecked);
					}
				}
			)

			if (selectClassName) {
				form.all(selectClassName).toggleClass('info', allBoxChecked);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'checkAllBox',
		function(form, name, allBox) {
			var totalBoxes = 0;
			var totalOn = 0;
			var inputs = A.one(form).all('input[type=checkbox]');

			allBox = A.one(allBox) || A.one(form).one('input[name=' + allBox + STR_RIGHT_SQUARE_BRACKET);

			if (!isArray(name)) {
				name = [name];
			}

			inputs.each(
				function(item, index) {
					if (!item.compareTo(allBox) && (arrayIndexOf(name, item.attr('name')) > -1)) {
						totalBoxes++;

						if (item.get(STR_CHECKED)) {
							totalOn++;
						}
					}
				}
			);

			allBox.attr(STR_CHECKED, (totalBoxes == totalOn));
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'createFlyouts',
		function(options) {
			options = options || {};

			var flyout = A.one(options.container);
			var containers = [];

			if (flyout) {
				var lis = flyout.all('li');

				lis.each(
					function(item, index) {
						var childUL = item.one('ul');

						if (childUL) {
							childUL.hide();

							item.addClass('lfr-flyout');
							item.addClass('has-children lfr-flyout-has-children');
						}
					}
				);

				var hideTask = A.debounce(
					function(event) {
						showTask.cancel();

						var li = event.currentTarget;

						if (li.hasClass('has-children')) {
							var childUL = event.currentTarget.one('> ul');

							if (childUL) {
								childUL.hide();

								if (options.mouseOut) {
									options.mouseOut.apply(event.currentTarget, [event]);
								}
							}
						}
					},
					300
				);

				var showTask = A.debounce(
					function(event) {
						hideTask.cancel();

						var li = event.currentTarget;

						if (li.hasClass('has-children')) {
							var childUL = event.currentTarget.one('> ul');

							if (childUL) {
								childUL.show();

								if (options.mouseOver) {
									options.mouseOver.apply(event.currentTarget, [event]);
								}
							}
						}
					},
					0
				);

				lis.on('mouseenter', showTask, 'li');
				lis.on('mouseleave', hideTask, 'li');
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'disableElements',
		function(obj) {
			var el = A.one(obj);

			if (el) {
				el = el.getDOM();

				var children = el.getElementsByTagName('*');

				var emptyFnFalse = Lang.emptyFnFalse;
				var Event = A.Event;

				for (var i = children.length - 1; i >= 0; i--) {
					var item = children[i];

					item.style.cursor = 'default';

					el.onclick = emptyFnFalse;
					el.onmouseover = emptyFnFalse;
					el.onmouseout = emptyFnFalse;
					el.onmouseenter = emptyFnFalse;
					el.onmouseleave = emptyFnFalse;

					Event.purgeElement(el, false);

					item.href = 'javascript:;';
					item.disabled = true;
					item.action = '';
					item.onsubmit = emptyFnFalse;
				}
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'disableToggleBoxes',
		function(checkBoxId, toggleBoxId, checkDisabled) {
			var checkBox = A.one('#' + checkBoxId);
			var toggleBox = A.one('#' + toggleBoxId);

			if (checkBox && toggleBox) {
				if (checkBox.get(STR_CHECKED) && checkDisabled) {
					toggleBox.attr('disabled', true);
				}
				else {
					toggleBox.attr('disabled', false);
				}

				checkBox.on(
					EVENT_CLICK,
					function() {
						toggleBox.attr('disabled', !toggleBox.get('disabled'));
					}
				);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'focusFormField',
		function(el, caretPosition) {
			var interacting = false;

			var clickHandle = A.getDoc().on(
				EVENT_CLICK,
				function(event) {
					interacting = true;

					clickHandle.detach();
				}
			);

			if (el.jquery) {
				el = el[0];
			}

			if (!interacting && Util.inBrowserView(el)) {
				A.one(el).focus();
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'forcePost',
		function(link) {
			link = A.one(link);

			if (link) {
				var url = link.attr('href');

				var newWindow = (link.attr('target') == '_blank');

				if (newWindow) {
					A.one(document.hrefFm).attr('target', '_blank');
				}

				submitForm(document.hrefFm, url, !newWindow);

				Util._submitLocked = null;
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'moveItem',
		function(fromBox, toBox, sort) {
			fromBox = A.one(fromBox);
			toBox = A.one(toBox);

			var selectedIndex = fromBox.get('selectedIndex');

			var selectedOption;

			if (selectedIndex >= 0) {
				var options = fromBox.all('option');

				selectedOption = options.item(selectedIndex);

				options.each(
					function(item, index) {
						if (item.get('selected')) {
							toBox.append(item);
						}
					}
				);
			}

			if (selectedOption && selectedOption.text() !== '' && sort === true) {
				Util.sortBox(toBox);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'openDDMPortlet',
		function(config, callback) {
			var instance = this;

			var defaultValues = {
				eventName: 'selectStructure'
			};

			config = A.merge(defaultValues,	config);

			var ddmURL;

			if (config.basePortletURL) {
				ddmURL = Liferay.PortletURL.createURL(config.basePortletURL);
			}
			else {
				ddmURL = Liferay.PortletURL.createRenderURL();
			}

			ddmURL.setEscapeXML(false);

			ddmURL.setDoAsGroupId(config.doAsGroupId || themeDisplay.getScopeGroupId());

			ddmURL.setParameter('classNameId', config.classNameId);
			ddmURL.setParameter('classPK', config.classPK);
			ddmURL.setParameter('eventName', config.eventName);
			ddmURL.setParameter('groupId', config.groupId);
			ddmURL.setParameter('mode', config.mode);
			ddmURL.setParameter('portletResourceNamespace', config.portletResourceNamespace);

			if ('redirect' in config) {
				ddmURL.setParameter('redirect', config.redirect);
			}

			if ('refererPortletName' in config) {
				ddmURL.setParameter('refererPortletName', config.refererPortletName);
			}

			if ('refererWebDAVToken' in config) {
				ddmURL.setParameter('refererWebDAVToken', config.refererWebDAVToken);
			}

			ddmURL.setParameter('scopeTitle', config.title);

			if ('showAncestorScopes' in config) {
				ddmURL.setParameter('showAncestorScopes', config.showAncestorScopes);
			}

			if ('showBackURL' in config) {
				ddmURL.setParameter('showBackURL', config.showBackURL);
			}

			if ('showHeader' in config) {
				ddmURL.setParameter('showHeader', config.showHeader);
			}

			if ('showManageTemplates' in config) {
				ddmURL.setParameter('showManageTemplates', config.showManageTemplates);
			}

			if ('showToolbar' in config) {
				ddmURL.setParameter('showToolbar', config.showToolbar);
			}

			ddmURL.setParameter('structureAvailableFields', config.structureAvailableFields);

			if (config.struts_action) {
				ddmURL.setParameter('struts_action', config.struts_action);
			}
			else {
				ddmURL.setParameter('struts_action', '/dynamic_data_mapping/view');
			}

			ddmURL.setParameter('templateId', config.templateId);

			ddmURL.setPortletId(166);
			ddmURL.setWindowState('pop_up');

			config.uri = ddmURL.toString();

			var dialogConfig = config.dialog;

			if (!dialogConfig) {
				dialogConfig = {};

				config.dialog = dialogConfig;
			}

			var eventHandles = [Liferay.once(config.eventName, callback)];

			var detachSelectionOnHideFn = function(event) {
				if (!event.newVal) {
					(new A.EventHandle(eventHandles)).detach();
				}
			};

			Util.openWindow(
				config,
				function(dialogWindow) {
					eventHandles.push(dialogWindow.after(['destroy', 'visibleChange'], detachSelectionOnHideFn));
				}
			);
		},
		['liferay-portlet-url']
	);

	Liferay.provide(
		Util,
		'openDocument',
		function(webDavUrl, onSuccess, onError) {
			if (A.UA.ie) {
				try {
					var executor = new A.config.win.ActiveXObject('SharePoint.OpenDocuments');

					executor.EditDocument(webDavUrl);

					if (Lang.isFunction(onSuccess)) {
						onSuccess();
					}

				}
				catch (exception) {
					if (Lang.isFunction(onError)) {
						onError(exception);
					}
				}
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'portletTitleEdit',
		function(options) {
			var obj = options.obj;

			if (obj && !obj.hasClass('portlet-borderless')) {
				var title = obj.one('.portlet-title-text');

				if (title && !title.hasClass('not-editable')) {
					title.addClass('portlet-title-editable');

					title.on(
						EVENT_CLICK,
						function(event) {
							var editable = Util._getEditableInstance(title);

							var rendered = editable.get('rendered');

							if (rendered) {
								editable.fire('stopEditing');
							}

							editable.set('node', event.currentTarget);

							if (rendered) {
								editable.syncUI();
							}

							editable._startEditing(event);
						}
					);

					title.setData('portletTitleEditOptions', options);
				}
			}
		},
		['aui-editable-deprecated']
	);

	Liferay.provide(
		Util,
		'removeFolderSelection',
		function(folderIdString, folderNameString, namespace) {
			A.byIdNS(namespace, folderIdString).val(0);

			A.byIdNS(namespace, folderNameString).val('');

			Liferay.Util.toggleDisabled(A.byIdNS(namespace, 'removeFolderButton'), true);
		},
		['aui-base', 'liferay-node']
	);

	Liferay.provide(
		Util,
		'removeItem',
		function(box, value) {
			box = A.one(box);

			var selectedIndex = box.get('selectedIndex');

			if (!value) {
				box.all('option').item(selectedIndex).remove(true);
			}
			else {
				box.all('option[value=' + value + STR_RIGHT_SQUARE_BRACKET).item(selectedIndex).remove(true);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'reorder',
		function(box, down) {
			box = A.one(box);

			var selectedIndex = box.get('selectedIndex');

			if (selectedIndex == -1) {
				box.attr('selectedIndex', 0);
			}
			else {
				var selectedItems = box.all(':selected');

				var lastIndex = box.get('options').size() - 1;

				var length = selectedItems.size();

				var item;
				var itemIndex;

				if (down) {
					while (length--) {
						item = selectedItems.item(length);

						itemIndex = item.get('index');

						var referenceNode = box.get('firstChild');

						if (itemIndex != lastIndex) {
							var nextSibling = item.next();

							if (nextSibling) {
								referenceNode = nextSibling.next();
							}
						}

						box.insertBefore(item, referenceNode);
					}
				}
				else {
					for (var i = 0; i < length; i++) {
						item = selectedItems.item(i);

						itemIndex = item.get('index');

						if (itemIndex === 0) {
							box.append(item);
						}
						else {
							box.insertBefore(item, item.previous());
						}
					}
				}
			}
		},
		['aui-base', 'aui-selector']
	);

	Liferay.provide(
		Util,
		'savePortletTitle',
		function(params) {
			A.mix(
				params,
				{
					doAsUserId: 0,
					plid: 0,
					portletId: 0,
					title: '',
					url: themeDisplay.getPathMain() + '/portlet_configuration/update_title'
				}
			);

			A.io.request(
				params.url,
				{
					data: {
						doAsUserId: params.doAsUserId,
						p_auth: Liferay.authToken,
						p_l_id: params.plid,
						portletId: params.portletId,
						title: params.title
					}
				}
			);
		},
		['aui-io']
	);

	Liferay.provide(
		Util,
		'selectEntity',
		function(config, callback) {
			var dialog = Util.getWindow(config.id);

			var eventName = config.eventName || config.id;

			var eventHandles = [Liferay.on(eventName, callback)];

			var detachSelectionOnHideFn = function(event) {
				if (!event.newVal) {
					(new A.EventHandle(eventHandles)).detach();
				}
			};

			if (dialog) {
				eventHandles.push(dialog.after(['destroy', 'visibleChange'], detachSelectionOnHideFn));

				dialog.show();
			}
			else {
				var destroyDialog = function(event) {
					var dialogId = config.id;

					var dialogWindow = Util.getWindow(dialogId);

					if (dialogWindow && Util.getPortletId(dialogId) === event.portletId) {
						dialogWindow.destroy();

						Liferay.detach('destroyPortlet', destroyDialog);
					}
				}

				Util.openWindow(
					config,
					function(dialogWindow) {
						eventHandles.push(dialogWindow.after(['destroy', 'visibleChange'], detachSelectionOnHideFn));

						Liferay.on('destroyPortlet', destroyDialog);
					}
				);
			}

		},
		['aui-base', 'liferay-util-window']
	);

	Liferay.provide(
		Util,
		'selectEntityHandler',
		function(container, selectEventName, disableButton) {
			var openingLiferay = Util.getOpener().Liferay;

			A.one(container).delegate(
				'click',
				function(event) {
					var currentTarget = event.currentTarget;

					if (disableButton !== false) {
						currentTarget.attr('disabled', true);
					}

					var result = Util.getAttributes(currentTarget, 'data-');

					openingLiferay.fire(selectEventName, result);

					Util.getWindow().hide();
				},
				'.selector-button'
			);
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'selectFolder',
		function(folderData, namespace) {
			A.byIdNS(namespace, folderData.idString).val(folderData.idValue);

			var name = AString.unescapeEntities(folderData.nameValue);

			A.byIdNS(namespace, folderData.nameString).val(name);

			var button = A.byIdNS(namespace, 'removeFolderButton');

			if (button) {
				Liferay.Util.toggleDisabled(button, false);
			}
		},
		['aui-base', 'liferay-node']
	);

	Liferay.provide(
		Util,
		'sortBox',
		function(box) {
			var newBox = [];

			var options = box.all('option');

			for (var i = 0; i < options.size(); i++) {
				newBox[i] = [options.item(i).val(), options.item(i).text()];
			}

			newBox.sort(Util.sortByAscending);

			var boxObj = A.one(box);

			boxObj.all('option').remove(true);

			A.each(
				newBox,
				function(item, index) {
					boxObj.append('<option value="' + item[0] + '">' + item[1] + '</option>');
				}
			);

			if (Browser.isIe()) {
				var currentWidth = boxObj.getStyle('width');

				if (currentWidth == 'auto') {
					boxObj.setStyle('width', 'auto');
				}
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'toggleBoxes',
		function(checkBoxId, toggleBoxId, displayWhenUnchecked, toggleChildCheckboxes) {
			var checkBox = A.one('#' + checkBoxId);
			var toggleBox = A.one('#' + toggleBoxId);

			if (checkBox && toggleBox) {
				var checked = checkBox.get(STR_CHECKED);

				if (checked) {
					toggleBox.show();
				}
				else {
					toggleBox.hide();
				}

				if (displayWhenUnchecked) {
					toggleBox.toggle();
				}

				checkBox.on(
					EVENT_CLICK,
					function() {
						toggleBox.toggle();

						if (toggleChildCheckboxes) {
							var childCheckboxes = toggleBox.all('input[type=checkbox]');

							childCheckboxes.attr(STR_CHECKED, checkBox.get(STR_CHECKED));
						}
					}
				);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'toggleControls',
		function(node) {
			var docBody = A.getBody();

			node = node || docBody;

			var trigger = node.one('.toggle-controls');

			if (trigger) {
				var hiddenClass = 'controls-hidden';
				var iconHiddenClass = 'icon-eye-close';
				var iconVisibleClass = 'icon-eye-open';
				var visibleClass = 'controls-visible';
				var currentClass = visibleClass;
				var currentIconClass = iconVisibleClass;

				if (Liferay._editControlsState != 'visible') {
					currentClass = hiddenClass;
					currentIconClass = iconHiddenClass;
				}

				var icon = trigger.one('.controls-state-icon');

				if (icon) {
					icon.addClass(currentIconClass);
				}

				docBody.addClass(currentClass);

				Liferay.fire(
					'toggleControls',
					{
						enabled: (Liferay._editControlsState === 'visible')
					}
				);

				trigger.on(
					'tap',
					function(event) {
						if (icon) {
							icon.toggleClass(iconVisibleClass);
							icon.toggleClass(iconHiddenClass);
						}

						docBody.toggleClass(visibleClass);
						docBody.toggleClass(hiddenClass);

						Liferay._editControlsState = (docBody.hasClass(visibleClass) ? 'visible' : 'hidden');

						Liferay.Store('liferay_toggle_controls', Liferay._editControlsState);

						Liferay.fire(
							'toggleControls',
							{
								enabled: (Liferay._editControlsState === 'visible'),
								src: 'ui'
							}
						);
					}
				);
			}
		},
		['event-tap', 'liferay-store']
	);

	Liferay.provide(
		Util,
		'toggleDisabled',
		function(button, state) {
			if (button.jquery) {
				button = button.get();
			}

			if (!A.instanceOf(button, A.NodeList)) {
				button = A.all(button);
			}

			button.each(
				function(item, index) {
					item.attr('disabled', state);

					item.toggleClass('disabled', state);
				}
			);
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'toggleRadio',
		function(radioId, showBoxIds, hideBoxIds) {
			var radioButton = A.one('#' + radioId);

			if (radioButton) {
				var checked = radioButton.get(STR_CHECKED);

				var showBoxes;

				if (Lang.isValue(showBoxIds)) {
					if (Lang.isArray(showBoxIds)) {
						showBoxIds = showBoxIds.join(',#');
					}

					showBoxes = A.all('#' + showBoxIds);

					showBoxes.toggle(checked);
				}

				radioButton.on(
					'change',
					function() {
						if (showBoxes) {
							showBoxes.show();
						}

						if (Lang.isValue(hideBoxIds)) {
							if (Lang.isArray(hideBoxIds)) {
								hideBoxIds = hideBoxIds.join(',#');
							}

							A.all('#' + hideBoxIds).hide();
						}
					}
				);
			}
		},
		['aui-base', 'aui-event']
	);

	Liferay.provide(
		Util,
		'toggleSelectBox',
		function(selectBoxId, value, toggleBoxId) {
			var selectBox = A.one('#' + selectBoxId);
			var toggleBox = A.one('#' + toggleBoxId);

			if (selectBox && toggleBox) {
				var dynamicValue = Lang.isFunction(value);

				var toggle = function() {
					var currentValue = selectBox.val();

					var visible = (value == currentValue);

					if (dynamicValue) {
						visible = value(currentValue, value);
					}

					toggleBox.toggle(visible);
				};

				toggle();

				selectBox.on('change', toggle);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		Util,
		'toggleSearchContainerButton',
		function(buttonId, searchContainerId, form, ignoreFieldName) {
			var searchContainer = A.one(searchContainerId);

			if (searchContainer) {
				searchContainer.delegate(
					EVENT_CLICK,
					function() {
						Liferay.Util.toggleDisabled(buttonId, !Liferay.Util.listCheckedExcept(form, ignoreFieldName));
					},
					'input[type=checkbox]'
				);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'submitForm',
		function(form, action, singleSubmit, validate) {
			if (!Util._submitLocked) {
				if (form.jquery) {
					form = form[0];
				}

				Liferay.fire(
					'submitForm',
					{
						action: action,
						form: A.one(form),
						singleSubmit: singleSubmit,
						validate: validate !== false
					}
				);
			}
		},
		['aui-base', 'aui-form-validator', 'liferay-form']
	);

	Liferay.publish(
		'submitForm',
		{
			defaultFn: Util._defaultSubmitFormFn
		}
	);

	Liferay.publish(
		'previewArticle',
		{
			defaultFn: Util._defaultPreviewArticleFn
		}
	);

	Liferay.provide(
		Util,
		'_openWindowProvider',
		function(config, callback) {
			var dialog = Window.getWindow(config);

			if (Lang.isFunction(callback)) {
				callback(dialog);
			}
		},
		['liferay-util-window']
	);

	Liferay.after(
		'closeWindow',
		function(event) {
			var id = event.id;

			var dialog = Liferay.Util.getTop().Liferay.Util.Window.getById(id);

			if (dialog && dialog.iframe) {
				var dialogWindow = dialog.iframe.node.get('contentWindow').getDOM();

				var openingWindow = dialogWindow.Liferay.Util.getOpener();
				var redirect = event.redirect;

				if (redirect) {
					openingWindow.location = redirect;
				}
				else {
					var refresh = event.refresh;

					if (refresh && openingWindow) {
						var data;

						if (!event.portletAjaxable) {
							data = {
								portletAjaxable: false
							};
						}

						openingWindow.Liferay.Portlet.refresh('#p_p_id_' + refresh + '_', data);
					}
				}

				dialog.hide();
			}
		}
	);

	Util.Window = Window;

	Liferay.Util = Util;

	Liferay.BREAKPOINTS = {
		PHONE: 768,
		TABLET: 980
	};

	Liferay.STATUS_CODE = {
		BAD_REQUEST: 400,
		INTERNAL_SERVER_ERROR: 500,
		OK: 200,
		SC_DUPLICATE_FILE_EXCEPTION: 490
	};

	// 0-200: Theme Developer
	// 200-400: Portlet Developer
	// 400+: Liferay

	Liferay.zIndex = {
		DOCK: 10,
		DOCK_PARENT: 20,
		ALERT: 430,
		DROP_AREA: 440,
		DROP_POSITION: 450,
		DRAG_ITEM: 460,
		OVERLAY: 1000,
		WINDOW: 1200,
		MENU: 5000,
		TOOLTIP: 10000
	};
})(AUI(), AUI.$, AUI._, Liferay);