// Written by Moses

//Gobals
const flowerImages = "../images/quiz_images/"
let imageArray = ["rose", "tulip", "daisy", "lavender", "lily", "poppy"];
let imagePath = "";
let image = "";
let quizImage = document.getElementById("quiz_image");

let answerList = document.getElementsByName("answer")
let quizAnswer = "";

let correct = []; //novel approach to holding correct guesses, values can be arbitrary

//random int from 0 to max;
function randomInt(max){
    return Math.floor(Math.random()*(max) ); //exclusive random
}

// simple key generation
function generateKey(){
    let seq = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    let key = ["TFC_"];
    for(let i = 0; i < 10; i++){
        key.push(seq[randomInt(seq.length)]);
    }   
    return key.join('');
}

function generateMessage(){
    let quiz = document.getElementById("quiz_body");
    let win = document.getElementById("win");   
    let loss = document.getElementById("loss");
    let key = document.getElementById("key");

    if(correct.length > 3){
        win.style.display = "initial";
        key.textContent = generateKey();
    }
    else{
        loss.style.display = "initial"; 
    }   
    quiz.remove();
}

//generate image
function generateImage(_currentImage_){
    randInt = randomInt(imageArray.length);
    image = imageArray[randInt];
    imagePath  = flowerImages + image+ ".jpg";
     _currentImage_.src = imagePath; 
    let index = imageArray.indexOf( image );  
    console.log("Image name: "+imageArray[index]);
    //console.log("Length: "+imageArray.length); 
}

// https://stackoverflow.com/questions/9709209/html-select-only-one-checkbox-in-a-group
function oneSelect(answer){
    /**
     * iterate through checkbox answers
     * uncheck all but one
     */  
    answerList.forEach((item) => {
        if(item !== answer)
            item.checked = false;
            quizAnswer = answer.value;
            console.log("Selected answer: "+ answer.value)
    });

}

function checkAnswer(){
    $("#myModal .modal-body").text("Incorrect!");
    answerList.forEach((item) => {
        if(item.checked == true){          
            if(imageArray.length > 0){
                if(image.includes(item.value)){
                    $("#myModal .modal-body").text("Correct! The answer was " + item.value);
                    correct.push(1);           
                }              
                else{        
                    $("#myModal .modal-body").text("Incorrect! The answer was " + image);    
                }
            }
            else{
                generateMessage();
            }    
        }     
        item.checked = false;
           
    });
    
}

// //clipboard
// function copyToClip(key){
//     //navigator.clipboard.writeText(key.textContent);
//     //alert("Copied "+key.textContent);
// }
//reload page
function reloadPage(){
    location.reload();
}

//show answer
$(document).ready(function(){
    $("#submit").click(function(){
        $("#myModal").modal('show');      
        checkAnswer();
        imageArray.splice(imageArray.indexOf(image), 1);
        generateImage(quizImage);
            
    });
    
});

//to clip board
$(document).ready(function(){
    $("#key").click(function(){
        navigator.clipboard.writeText( $("#key").text() );
        $("#clip-response").modal('show');   
        
    });
});

//img src undefined error quick fix
$("#quiz_image").on("error", function (){
    generateMessage();
});

generateImage(quizImage);
getScore(correct);

// console.log("Correct: "+correct.length);
// console.log("Image Array: ");
// for(let i = 0; i < imageArray.length; i++){
//     console.log(imageArray[i]);
// }


// console.log(image)
// console.log(quizAnswer)
//document.body.appendChild(randomImage());

/**
 * Major todo is to refactor the way he functions qccess the data
 * currenntly it's very haphazard
 * somefunctions directly reference variables
 * orthers return and manipulate
 * all in all needs to have a standardised  design
 * this will work for the time being however
 * 
 */
/*
* todo
* 
* get images
* randomly display images every reload
* 
* list of answers:
* if image path string contains answer substring:
*     print correct
*     do something else
* else
*     incorrect
* 
*/

