function beerLyrics(){
    for(i=99;i>=3;i--){
        document.getElementById("body").innerHTML+=("<br>"+i+" bottles of beer on the wall, "+i+" bottles of beer<br>Take one down and pass it around, "+(i-1)+" bottles of beer on the wall<br>");
    }
    document.getElementById("body").innerHTML+=("<br>2 bottles of beer on the wall, 2 bottles of beer<br>Take one down and pass it around, 1 bottle of beer on the wall<br><br>1 bottle of beer on the wall, 1 bottle of beer<br>Take one down and pass it around, no more bottles of beer on the wall<br><br>No more bottles of beer on the wall, no more bottles of beer<br>Go to the store and buy some more, 99 bottles of beer on the wall");
}
    
const button= document.getElementById("btn");
    
button.addEventListener("click",function(e){
    beerLyrics();
    event.preventDefault;
});