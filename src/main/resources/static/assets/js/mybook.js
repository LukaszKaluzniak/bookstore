$('document').ready(function() {

    $('table #detailsButton').on('click', function(event) {
        var href = $(this).attr('href');
        $.get(href, function(book, status) {
            $('#id-details').val(book.id);
            $('#title-details').val(book.title);
            $('#authors-details').val(book.authors);
            $('#isbn-details').val(book.isbn);
            $('#language-details').val(book.language);
            $('#year-of-publication-details').val(book.yearOfPublication);
            $('#publisher-details').val(book.publisher);
            $('#number-of-pages-details').val(book.numberOfPages);
            $('#description-details').val(book.authors);
            $('#price-details').val(book.numberOfPages);
        });
        $('#detailsModal').modal();
    });

});