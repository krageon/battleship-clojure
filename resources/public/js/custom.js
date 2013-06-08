$(document).ready(function() {
	$currentShip = "";
	var letters = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
	var index = 0;

	$('form input[value="To Battle!!"]').click(function(){
		var canSubmit = true;
		var counter = 0;
		$('tr td#amount').each(function(){
			if($(this).text().charAt(0) != 0){
				canSubmit = false;
			}
		});
		if(canSubmit){
			$('input[value="o"]').each(function(){
				counter++;
			});
		}else{
			alert("You haven't placed your whole fleet yet!"); return false;
		}
		if(counter === 18){
			$(this).submit();
			alert("Going to submit!!");
		}else{
			alert("You bugged the system, good job, now try again");
			window.location.reload();
      return false;
		}
	});

	$(".add").click(function(){
		$row = $(this).closest('tr');
		$amount = $row.find('#amount').text().charAt(0);
		if($amount > 0){
			$row.find('#amount').text($amount-1+"x");
			$size = $row.find('#size').text();
			coord = getFirstCoord();
			for($i = 0; $i < $size; $i++){
				$currentShip = $('input[name="'+coord+($i+1)+'"]');
				setAttribute($row.find('#name').text()+letters[index], "o", coord+"1", true, $size);
			}
			index++;
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
				var horizontal = $(this).attr('horizontal') == 'true';
				var canSet = true;

				if(horizontal){
					if((parseInt(posArray)+parseInt(size)) > letters.length){ alert('Cannot change direction, there is no space!');}
					else{
						for(i = 1; i < size+1; i++){
							if($('input[name="'+letters[(posArray+i)]+xCoord+'"]').val() === "o"){
								alert('Cannot change direction, there is already a ship in place');
								canSet = false; break;
							}
						}
						if(canSet){
							removeShipByName(name);
							for(i = 0; i < size; i++){
								$currentShip = $('input[name="'+letters[(parseInt(posArray)+i)]+xCoord+'"]');
								setAttribute(name, "o", coord+"1", false, $size);
							}
						}
					}
				}else{
					if(parseInt(xCoord)+(size-1) > 10){ alert('Cannot change direction, there is no space!');}
					else{
						for(i = 1; i < size+1; i++){
							if($('input[name="'+yCoord+(parseInt(xCoord)+i)+'"]').val() === "o"){
								alert('Cannot change direction, there is already a ship in place');
								canSet = false; break;
							}
						}
						if(canSet){
							removeShipByName(name)
							for(i = 0; i < size; i++){
								$currentShip = $('input[name="'+yCoord+(parseInt(xCoord)+i)+'"]');
								setAttribute(name, "o", yCoord+xCoord, true, $size);
							}
						}
					}
				}
			}
		}else{
			var name =$currentShip.attr('ship');
			var size = $currentShip.attr('size');
			var horizontal = $currentShip.attr('horizontal') == 'true';
			buttonCoord = $(this).attr('name');
			var yButton = buttonCoord.charAt(0);
			if(buttonCoord.length > 2){
				var xButton = buttonCoord.charAt(1)+buttonCoord.charAt(2);
			}else{
				var xButton = buttonCoord.charAt(1);
			}
			var posArray = $.inArray(yButton, letters);

			if(horizontal){
				if(isLegal(horizontal, size, yButton, xButton)){
					removeShipByName(name);
					for(i = 0; i < size; i++){
						$currentShip = $('input[name="'+yButton+(parseInt(xButton)+i)+'"]');
						setAttribute(name, "o", yButton+xButton, true, size);
					}
				}else{
					alert('There is not enough room to put the ship here');
				}
			}else{
				if(isLegal(horizontal, size, yButton, xButton)){
					removeShipByName(name);
					for(i = 0; i < size; i++){
						$currentShip = $('input[name="'+(letters[(parseInt(posArray)+i)]+xButton+'"]'));
						setAttribute(name, "o", yButton+xButton, false, size);
					}
				}else{
					alert('There is not enough room to put the ship here');
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

	function isLegal(horizontal, size, startY, startX){
		var isLegal = true;
		var posArray = $.inArray(startY, letters);

		if(horizontal){
			for(i = 0; i < size; i++){
				if($('input[name="'+startY+(parseInt(startX)+i)+'"]').val() === "o" || ((parseInt(startX))+(parseInt(size-1))) > 10){
					isLegal = false; break;
				}
			}
		}else{
			for(i = 0; i < size; i++){
				if($('input[name="'+letters[(parseInt(posArray)+i)]+startX+'"]').val() === "o" || ((parseInt(posArray))+(parseInt(size))) > letters.length){
					isLegal = false; break;
				}
			}
		}
		return isLegal;
	}

	function removeShipByName(name){
		$('input[ship="'+name+'"]').each(function(){
								$currentShip = $(this);
								setAttribute("", "~", "", false, 0);
							});
	}
});