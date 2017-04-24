jQuery.fn.serializeObject = function() {
  var arrayData, objectData;
  arrayData = this.serializeArray();
  objectData = {};

  $.each(arrayData, function() {
    var value;

    if (this.value != null) {
      value = this.value;
    } else {
      value = '';
    }

    if (objectData[this.name] != null) {
      if (!objectData[this.name].push) {
        objectData[this.name] = [objectData[this.name]];
      }

      objectData[this.name].push(value);
    } else {
      objectData[this.name] = value;
    }
  });

  return objectData;
};

$(document).ready(function() {
	
	function load() {
		$.ajax({
			  method: 'GET',
			  url: '/teams',
			  dataType: 'json',
			  contentType: 'application/json; charset=utf-8'
			})
			.done(function(response) {
				
				var tbody = $('<tbody></tbody>');
				
				$.each(response, function(i, each) {					
					var tr = $('<tr></tr>')
					       .append('<td>' + each.city + '</td>')
					       .append('<td>' + each.name + '</td>')
					       .append('<td>' + each.modified + '</td>')
					       .append('<td><button class="btn btn-warning" name="edit" data-id="' + each.id + '">Edit</button></td>')
					       .append('<td><button class="btn btn-danger" name="delete" data-id="' + each.id + '">Delete</button></td>');
				       
					tbody.append(tr);
				});
				
				$('table#teams').children('tbody').remove();
				$('table#teams').append(tbody);
				
			});
	}
	
	$('button[name="add"]').click(function(event) {
		event.preventDefault();
		
		$('form#new')[0].reset();
		$('form#new').show();
		
		$('form#edit')[0].reset();
		$('form#edit').hide();		
	});
	
	$('table#teams').on('click', 'button[name="delete"]', function(event) {
		event.preventDefault();
		
		var id = $(this).data('id');
		console.log('delete was clicked for team id = '+id);
		
		if (confirm('Are you sure you want to delete this?')) {
			$.ajax({
				  method: 'DELETE',
				  url: '/teams/'+id
				})
				.done(function(response) {
					load();
				});
		}
	});
	
	$('table#teams').on('click', 'button[name="edit"]', function(event) {
		event.preventDefault();
		
		var id = $(this).data('id');		
		console.log('edit was clicked for team id = '+id);
		
		$.ajax({
			  method: 'GET',
			  url: '/teams/' + id,
			  dataType: 'json',
			  contentType: 'application/json; charset=utf-8'
			})
			.done(function(response) {
				$('form#new')[0].reset();
				$('form#new').hide();
				
				$('form#edit')[0].reset();
				
				$('input[name="id"]').val(response.id);		
				$('input[name="city"]').val(response.city);
				$('input[name="name"]').val(response.name);
				
				$('form#edit').show();
			});
		
	});	
	
	$('form#new').submit(function(event) {
		event.preventDefault();
				
		var form = $(this).serializeObject();
		console.log('new team submitted');
		console.log(form);

		$.ajax({
			  method: 'POST',
			  url: '/teams',
			  dataType: 'json',
			  contentType: 'application/json; charset=utf-8',
			  data: JSON.stringify(form)
			})
			.done(function(response) {							
				$('form#new')[0].reset();
				$('form#new').hide();
				
				load();
			});
		
	});
	
	$('form#edit').submit(function(event) {
		event.preventDefault();
				
		var form = $(this).serializeObject();
		console.log('edits to team id = ' + form.id + ' submitted');
		console.log(form);

		$.ajax({
			  method: 'PUT',
			  url: '/teams/' + form.id,
			  dataType: 'json',
			  contentType: 'application/json; charset=utf-8',
			  data: JSON.stringify(form)
			})
			.done(function(response) {							
				$('form#edit')[0].reset();
				$('form#edit').hide();
				
				load();
			});
		
	});
	
	load();
		
});