(window.webpackJsonp=window.webpackJsonp||[]).push([[3],{165:function(e,a,o){"use strict";var t=o(150),n=o(10),r=o(0),i=(o(96),o(151)),c=o(152),d=o(156),l=o(292),s=o(154),p=r.forwardRef((function(e,a){var o=e.children,c=e.classes,d=e.className,p=e.color,b=void 0===p?"default":p,m=e.component,u=void 0===m?"button":m,h=e.disabled,g=void 0!==h&&h,y=e.disableElevation,x=void 0!==y&&y,f=e.disableFocusRipple,v=void 0!==f&&f,S=e.endIcon,k=e.focusVisibleClassName,C=e.fullWidth,z=void 0!==C&&C,w=e.size,O=void 0===w?"medium":w,j=e.startIcon,R=e.type,I=void 0===R?"button":R,$=e.variant,N=void 0===$?"text":$,E=Object(t.a)(e,["children","classes","className","color","component","disabled","disableElevation","disableFocusRipple","endIcon","focusVisibleClassName","fullWidth","size","startIcon","type","variant"]),T=j&&r.createElement("span",{className:Object(i.a)(c.startIcon,c["iconSize".concat(Object(s.a)(O))])},j),L=S&&r.createElement("span",{className:Object(i.a)(c.endIcon,c["iconSize".concat(Object(s.a)(O))])},S);return r.createElement(l.a,Object(n.a)({className:Object(i.a)(c.root,c[N],d,"inherit"===b?c.colorInherit:"default"!==b&&c["".concat(N).concat(Object(s.a)(b))],"medium"!==O&&[c["".concat(N,"Size").concat(Object(s.a)(O))],c["size".concat(Object(s.a)(O))]],x&&c.disableElevation,g&&c.disabled,z&&c.fullWidth),component:u,disabled:g,focusRipple:!v,focusVisibleClassName:Object(i.a)(c.focusVisible,k),ref:a,type:I},E),r.createElement("span",{className:c.label},T,o,L))}));a.a=Object(c.a)((function(e){return{root:Object(n.a)(Object(n.a)({},e.typography.button),{},{boxSizing:"border-box",minWidth:64,padding:"6px 16px",borderRadius:e.shape.borderRadius,color:e.palette.text.primary,transition:e.transitions.create(["background-color","box-shadow","border"],{duration:e.transitions.duration.short}),"&:hover":{textDecoration:"none",backgroundColor:Object(d.b)(e.palette.text.primary,e.palette.action.hoverOpacity),"@media (hover: none)":{backgroundColor:"transparent"},"&$disabled":{backgroundColor:"transparent"}},"&$disabled":{color:e.palette.action.disabled}}),label:{width:"100%",display:"inherit",alignItems:"inherit",justifyContent:"inherit"},text:{padding:"6px 8px"},textPrimary:{color:e.palette.primary.main,"&:hover":{backgroundColor:Object(d.b)(e.palette.primary.main,e.palette.action.hoverOpacity),"@media (hover: none)":{backgroundColor:"transparent"}}},textSecondary:{color:e.palette.secondary.main,"&:hover":{backgroundColor:Object(d.b)(e.palette.secondary.main,e.palette.action.hoverOpacity),"@media (hover: none)":{backgroundColor:"transparent"}}},outlined:{padding:"5px 15px",border:"1px solid ".concat("light"===e.palette.type?"rgba(0, 0, 0, 0.23)":"rgba(255, 255, 255, 0.23)"),"&$disabled":{border:"1px solid ".concat(e.palette.action.disabledBackground)}},outlinedPrimary:{color:e.palette.primary.main,border:"1px solid ".concat(Object(d.b)(e.palette.primary.main,.5)),"&:hover":{border:"1px solid ".concat(e.palette.primary.main),backgroundColor:Object(d.b)(e.palette.primary.main,e.palette.action.hoverOpacity),"@media (hover: none)":{backgroundColor:"transparent"}}},outlinedSecondary:{color:e.palette.secondary.main,border:"1px solid ".concat(Object(d.b)(e.palette.secondary.main,.5)),"&:hover":{border:"1px solid ".concat(e.palette.secondary.main),backgroundColor:Object(d.b)(e.palette.secondary.main,e.palette.action.hoverOpacity),"@media (hover: none)":{backgroundColor:"transparent"}},"&$disabled":{border:"1px solid ".concat(e.palette.action.disabled)}},contained:{color:e.palette.getContrastText(e.palette.grey[300]),backgroundColor:e.palette.grey[300],boxShadow:e.shadows[2],"&:hover":{backgroundColor:e.palette.grey.A100,boxShadow:e.shadows[4],"@media (hover: none)":{boxShadow:e.shadows[2],backgroundColor:e.palette.grey[300]},"&$disabled":{backgroundColor:e.palette.action.disabledBackground}},"&$focusVisible":{boxShadow:e.shadows[6]},"&:active":{boxShadow:e.shadows[8]},"&$disabled":{color:e.palette.action.disabled,boxShadow:e.shadows[0],backgroundColor:e.palette.action.disabledBackground}},containedPrimary:{color:e.palette.primary.contrastText,backgroundColor:e.palette.primary.main,"&:hover":{backgroundColor:e.palette.primary.dark,"@media (hover: none)":{backgroundColor:e.palette.primary.main}}},containedSecondary:{color:e.palette.secondary.contrastText,backgroundColor:e.palette.secondary.main,"&:hover":{backgroundColor:e.palette.secondary.dark,"@media (hover: none)":{backgroundColor:e.palette.secondary.main}}},disableElevation:{boxShadow:"none","&:hover":{boxShadow:"none"},"&$focusVisible":{boxShadow:"none"},"&:active":{boxShadow:"none"},"&$disabled":{boxShadow:"none"}},focusVisible:{},disabled:{},colorInherit:{color:"inherit",borderColor:"currentColor"},textSizeSmall:{padding:"4px 5px",fontSize:e.typography.pxToRem(13)},textSizeLarge:{padding:"8px 11px",fontSize:e.typography.pxToRem(15)},outlinedSizeSmall:{padding:"3px 9px",fontSize:e.typography.pxToRem(13)},outlinedSizeLarge:{padding:"7px 21px",fontSize:e.typography.pxToRem(15)},containedSizeSmall:{padding:"4px 10px",fontSize:e.typography.pxToRem(13)},containedSizeLarge:{padding:"8px 22px",fontSize:e.typography.pxToRem(15)},sizeSmall:{},sizeLarge:{},fullWidth:{width:"100%"},startIcon:{display:"inherit",marginRight:8,marginLeft:-4,"&$iconSizeSmall":{marginLeft:-2}},endIcon:{display:"inherit",marginRight:-4,marginLeft:8,"&$iconSizeSmall":{marginRight:-2}},iconSizeSmall:{"& > *:first-child":{fontSize:18}},iconSizeMedium:{"& > *:first-child":{fontSize:20}},iconSizeLarge:{"& > *:first-child":{fontSize:22}}}}),{name:"MuiButton"})(p)},287:function(e,a,o){"use strict";o.r(a),o.d(a,"default",(function(){return s}));o(161);var t=o(0),n=o.n(t),r=o(266),i=o(165),c=Object(r.a)((function(e){return{root:{textAlign:"center",padding:10},btn:{width:"90%",margin:10,textTransform:"none"}}})),d=["default","primary","secondary"],l=[{name:"Call Auth",fun:"BioAuthentication",args:[]},{name:"Get App Unique ID",fun:"UniqueAppID",args:[]},{name:"Get Access Logs",fun:"",args:[]},{name:"Root(JailBreak) Check",fun:"RootingCheck",args:[]},{name:"App Intergrity Check",fun:"IntergrityCheck",args:[]}];function s(){var e=c();return n.a.createElement("div",{className:e.root},l.map((function(a,o){return n.a.createElement(i.a,{key:o,variant:"contained",color:d[o%3],onClick:function(){$flex[a.fun].apply(null,a.args)},size:"large",className:e.btn},a.name)})))}}}]);