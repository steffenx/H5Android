
/**
 * 图标滑动
 * @sty
 * @DateTime
 * @return    {[type]}                 [description]
 */
(function(){

	var NavSlip = function( ele , options ){

		if(!ele) return null;

		this.container = document.querySelectorAll(ele)[0];

		this.options = options || {};
		this.speed = this.options.speed || '0.5s';
		this.elastic = this.options.elastic || false;
		this.timing = this.options.timing || 'ease-in-out';

		this.params = this.initParam();

		this.page = 1; //下标值

		this.initStyle();


		if (this.oTabUl.addEventListener) {

			this.oTabUl.addEventListener("touchstart", this, false);
	        this.oTabUl.addEventListener("touchmove", this, false);
	        this.oTabUl.addEventListener("touchend", this, false);
	        this.oTabUl.addEventListener("touchcancel", this, false);

		}

		this.container.addEventListener && this.container.addEventListener("touchmove",function(ev){ev.preventDefault();},false);
		
	};

	NavSlip.prototype={

		initParam:function(){
			return{
				startX : 0,	//	start坐标
				moveX : 0,	// move坐标
				endX : 0,	//	end坐标
				differX : 0,// touch坐标差值
				tempX : 0,//临时记录坐标
				transl : null //transform变量
			}
		},

		initStyle:function(){

			this.oTabUl = this.container.querySelectorAll('ul')[0];
			this.oLi = this.container.querySelectorAll('li');
			this.dotContainer =  this.container.querySelectorAll('.dot-container')[0];

			//dot act start
			this.createDot(this.oLi.length);
			this.dotAct = 0;
			this.onActChange(this.dotAct);

			this.liWidthArr = new Array();
			this.winWidth = +function(){
				if (window.innerWidth)
					return window.innerWidth;
				else if ((document.body) && (document.body.clientWidth))
					return document.body.clientWidth;
			}();

			for(var i=0; i<this.oLi.length; i++){
				this.oLi[i].style.width = this.winWidth+'px';
				this.liWidthArr.push(Math.ceil.call(null,this.getStyle(this.oLi[i],'width').match(/\d+/)[0]));
			};

			var reducer = function add(sumSoFar, item) { return sumSoFar + Math.ceil(item); };

			var total = this.liWidthArr.reduce(reducer, 0);

			this.limitWidth = total - this.winWidth;

			this.oTabUl.style.width = total+'px';

		},

		getStyle:function(obj,attr){
	  		return obj.currentStyle ? obj.currentStyle : document.defaultView.getComputedStyle(obj, null)[attr];
		},

		onStart:function( event ){
			var params = this.params,
				_this = this.oTabUl;

			params.startX = event.touches[0].pageX;
			_this.style.transition = null;
			if(params.moveX) params.moveX=0;
		},

		onMove:function( event ){
			event.stopPropagation();
			var params = this.params,
				_this = this.oTabUl;

			params.moveX = event.touches[0].pageX;
			params.differX = params.moveX - params.startX;
			params.tempX = params.differX + params.endX;


		},

		onEnd:function( event ){
			var params = this.params,
				_this = this.oTabUl,
				iDis = 0,
				iLength = this.oLi.length;

			params.endX = params.tempX;

			if (params.differX < 0) {
				this.page = Math.min(iLength,++this.page);
				iDis = -((this.page-1) * this.winWidth);
				this.transformTime(iDis,_this);
				this.dotAct = Math.min(iLength-1,++this.dotAct);
			}
			if (params.differX > 0) {
				this.page = Math.max(1,--this.page);
				iDis = -((this.page-1) * this.winWidth);
				this.transformTime(iDis,_this);
				this.dotAct = Math.max(0,--this.dotAct);
			}
			

			return true;
		},

		transformLate:function(trans,obj){

			transl="translate3d("+trans+"px,0,0)";

			obj.style.transform = transl;
			obj.style.mozTransform = transl;
			obj.style.WebkitTransform = transl;
		},

		transformTime:function(trans,obj){

			obj.style.transition ='transform '+this.speed+' '+ this.timing;
			obj.style.mozTtransition ='transform '+this.speed+' '+ this.timing;
			obj.style.webkitTransition ='transform '+this.speed+' '+ this.timing;


			transl="translate3d("+trans+"px,0,0)";

			obj.style.transform = transl;
			obj.style.mozTransform = transl;
			obj.style.WebkitTransform = transl;

			setTimeout(function(){
				this.onActChange(this.dotAct);
			}.bind(this),400)
		},

		handleEvent:function( event ){
			switch (event.type){

				case 'touchstart':
					this.onStart(event);
					break;

				case 'touchmove':
					this.onMove(event);
					break;

				case 'touchend':
					this.onEnd(event);
					break;

			}
		},

		createDot:function( size ){
			var liLength = 0,
				oSpan = null;

			for(;liLength < size; liLength++){
				oSpan = document.createElement('span');
				this.dotContainer.appendChild(oSpan);
			}

			
		},

		onActChange:function( num ){
			var spanEle = this.dotContainer.querySelectorAll('span');

			for(var i=0; i<spanEle.length; i++){

				if( DOMTokenList ){

					DOMTokenList.prototype.adds = function(tokens){
						tokens.split(" ").forEach(function(token){
							this.add(token);
						}.bind(this));
						return this;
					};

					DOMTokenList.prototype.removes = function(tokens) {
					   tokens.split(" ").forEach(function(token) {
					       this.remove(token);
					   }.bind(this));
					   return this;
					};
					
					spanEle[i].classList.contains("act") && spanEle[i].classList.removes("act");
					spanEle[num].classList.adds("act");

				}

				else{
					spanEle[i].className='';
					spanEle[num].className='act';
				}
			}

		}
	};
	
	var NavTouch = function(ele,options){

		return new NavSlip(ele,options);

	}; 
	
	window.NavTouch = NavTouch;

	
	
})();
