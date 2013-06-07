$(document).ready(function() {
	$currentShip = "";
	var letters = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];

	$(".add").click(function(){
		$row = $(this).closest('tr');
		$amount = $row.find('#amount').text().charAt(0);
		if($amount > 0){
			$row.find('#amount').text($amount-1+"x");
			$size = $row.find('#size').text();
			coord = getFirstCoord();
			for($i = 0; $i < $size; $i++){
				$currentShip = $('input[name="'+coord+($i+1)+'"]');
				setAttribute($row.find('#name').text(), "o", coord+"1", true, $size);
			}
		}else{
			alert("You've placed the max amount of this ship");
		}
	});

	$("table#fleet td input").click(function(){
		if($(this).val() === 'o'){
			var size = $(this).attr('size');
			if(size > 1){
				var yCoord= $(this).attr('start').charAt(0);
				var xCoord= $(this).attr('start').charAt(1);
				var posArray = $.inArray(yCoord, letters);
				var name = $(this).attr('ship');
				var size = $(this).attr('size');
				var horizontal = Boolean($(this).attr('horizontal'));
				var canSet = true;

        //False isn't registered as false..?
				if(horizontal){
					if((parseInt(posArray)+parseInt(size)) > letters.length){ alert('Cannot change direction, there is no space!');}
					else{
						for(i = 1; i < size+1; i++){
							if($('input[name="'+letters[(posArray+i)]+xCoord+'"]').val() === "o"){
								alert('Cannot change direction, there is already a ship in place');
								canSet = false;
							}
						}
						if(canSet){
							$('input[ship="'+name+'"]').each(function(){
								$currentShip = $(this);
								setAttribute("", "~", "", false, 0);
							});
							for(i = 0; i < size; i++){
								$currentShip = $('input[name="'+letters[(parseInt(posArray)+i)]+xCoord+'"]');
								setAttribute($row.find('#name').text(), "o", coord+"1", false, $size);
							}
						}
					}
				}else{
					if(parseInt(xCoord)+(size-1) > 10){ alert('Cannot change direction, there is no space!');}
					else{
						for(i = 1; i < size+1; i++){
							if($('input[name="'+yCoord+(parseInt(xCoord)+i)+'"]').val() === "o"){
								alert('Cannot change direction, there is already a ship in place');
								canSet = false;
							}
						}
						if(canSet){
							$('input[ship="'+name+'"]').each(function(){
								$currentShip = $(this);
								setAttribute("", "~", "", false, 0);
							});
							for(i = 0; i < size; i++){
								$currentShip = $('input[name="'+letters[(parseInt(posArray)+i)]+xCoord+'"]');
								setAttribute($row.find('#name').text(), "o", coord+"1", true, $size);
							}
						}
					}
				}
			}
		}
	});

	function setAttribute(ship, value, startCoordination, horizontal, size){
		$currentShip.attr('ship', ship);
		$currentShip.val(value);
		$currentShip.attr('start', startCoordination);
		$currentShip.attr('horizontal', horizontal);
		$currentShip.attr('size', size);
	}

	function getFirstCoord(){
		var isEmpty = true;
		for(a = 0; a < letters.length; a++){
			for(i = 1; i < 11; i++){
				if($('input[name="'+letters[a]+(i)+'"]').val() === "o"){
					isEmpty = false; break;
				}else{
					isEmpty = true;
				}
			}
			if(isEmpty){
				return letters[a];break;
			}
		}
	}
});