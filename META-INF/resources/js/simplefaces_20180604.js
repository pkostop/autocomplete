var block=false;

function sfAutoComplete(id,event){
	if(event.status == 'success'){				
		var el= document.getElementById(id);		
		var elAuto=document.getElementById(id+"_autocomplete");
		el.style.top = elAuto.offsetHeight+'px';		
		el.style.width=elAuto.offsetWidth+'px';						
		el.style.left=elAuto.offsetLeft+'px';
		el.style.display="inline";
		el.style.boxShadow= "10px 10px 5px #888888";				
		if(elAuto.setSelectionRange!=null)
			elAuto.setSelectionRange( elAuto.value.length, elAuto.value.length);		
		block=false;		
	}
	return false;
}

function hideOptions(id){
	window.setTimeout(function(){
		document.getElementById(id).style.display='none';
	}, 300);	
}

function sfAutoCompleteItemSelected(elementId, label, value){
	var opt=document.getElementById(elementId);	
	document.getElementById(elementId+'_autocomplete').value=label;	
	document.getElementById(elementId+'_value').value=value;		
	opt.style.display='none';
}



function sfAutoCompleteFire(element,event,clientId,charNo){
	var valueId=element.id.replace('_autocomplete','_value')		
	var _options=document.getElementById(element.id.replace('_autocomplete',''));
	var _value=document.getElementById(valueId);
	if(element.value == ''){						
		_value.value=null;				
	}	
	if( event.keyCode != null && !(event.keyCode>=48 && event.keyCode<=57) && !(event.keyCode>=65 && event.keyCode<=90) && !(event.keyCode>=96 && event.keyCode<=105) && event.keyCode != 8 && event.keyCode != 46){
		return;
	}		

	if(_options!=null)
		_options.style.display='none';	
	
	if(block==false){
		block=true;					
		setTimeout(
		function(){												
			if( element.value.length >= charNo){								
				jsf.ajax.request(element, null, {'execute':clientId,'render':clientId,onevent:function(e){return sfAutoComplete(clientId,e);}});				
			}
			else{
				block=false;
			}
		}
		,700);
	}else{

	}
}