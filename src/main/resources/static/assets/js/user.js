$('document').ready(function() {

    $('table #deleteButton').on('click', function(event) {
        var href=$(this).attr('href');
        $('#confirmDeleteButton').attr('href', href);
        $('deleteModal').modal();
    });

});