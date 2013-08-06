			$(document).ready(function(){
				var $lis = $('ul#my li'); //zczytujemy polozenie klasy 
				var $slides = $('#slides > div');
				//var $slides = $('#slide-box-wrapper > div');//zczytujemy polozenie klasy 
				
				
				$lis.first().addClass('select-slider');
				$slides.first().addClass('animate');
				
				
				// $slides.last().css('display','none')
				// $slides.css('display','block');
				
				$lis.click(function(event) {
					var $this = $(this);		//zczytujemy index 
					var index = $this.index();	//zczytujemy index			
				
				$lis.removeClass('select-slider').eq(index).addClass('select-slider'); //zamieniamy klasami
				//$slides.css('display','none').eq(index).css('display','block'); //zamieniamy klasami
				$slides.removeClass('animate').eq(index).addClass('animate'); //zamieniamy klasami
				
				  
				  return false;
				
					});
			});