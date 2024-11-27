//////////// Written by Catherine /////////////////

////// jQuery to control modal popups //////////////


$(document).ready(function(){
        $(".first-product").click(function(){
            $("#myModal").modal('show');
        });
    
        $(".second-product").click(function(){
            $("#myModal_two").modal('show');
        });

        $(".third-product").click(function(){
            $("#myModal_three").modal('show');
        });

        $(".fourth-product").click(function(){
            $("#myModal_four").modal('show');
        });

        $(".fifth-product").click(function(){
            $("#myModal_five").modal('show');
        });

        $(".sixth-product").click(function(){
            $("#myModal_six").modal('show');
        });

        $(".seventh-product").click(function(){
            $("#myModal_seven").modal('show');
        });
   
        $(".eighth-product").click(function(){
            $("#myModal_eight").modal('show');
        });
    });

for(i=1;i<=8;i++){
    document.getElementById("orderbtn-"+i).addEventListener("click", function(){
    alert("This is where we would integrate the checkout flow of our chosen payment provider.");
    });
}
