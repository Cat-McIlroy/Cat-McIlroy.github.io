function createForm(){

    document.getElementById("instructions").innerHTML=("Please enter your marks for each subject: ");
   
    var numberSubjects=document.getElementById("numberSubjects").valueAsNumber;
    
    for(i=1;i<=numberSubjects;i++){
        document.getElementById("gradeForm").innerHTML+=('<input type=number id="grade'+i+'"><br><br>');
    }
    
    document.getElementById("gradeForm").innerHTML+=('<input type="submit" value="Submit"></form>');
    
    document.getElementById("formDiv").innerHTML=('');
   
}

const subjectForm= document.getElementById("subjectForm");
subjectForm.addEventListener("submit",function(e){
    createForm();
    event.preventDefault();
});

function getMarks(){
    formLength= document.getElementById("gradeForm").length;
    
    for(i=1;i<formLength;i++){
        var marks=document.getElementById("grade"+i).valueAsNumber;
        
        if(marks>80){
            var grade="A";
        }
        else if(marks<80 && marks>70){
            grade= "B";
        }
        else if(marks<70 && marks>60){
            grade= "C";
        }
        else if(marks<60 && marks>50){
            grade="D";
        }
        else{
            grade="F";
        }
        
        document.getElementById("marksPrinted").innerHTML+=("Your marks for Subject "+i+" are: "+marks+"<br>Grade: "+grade+"<br><br>");
    }
    
    document.getElementById("formDiv-2").innerHTML=('');
}

const marksForm= document.getElementById("gradeForm");

marksForm.addEventListener("submit", function(e){
    getMarks();
    event.preventDefault();
});

