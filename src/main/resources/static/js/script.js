/**
 * 
 */

const menufun =()=>{
        
    let sidebar = document.getElementsByClassName("sidebar")[0];

    if(   sidebar.style.display == "block"  )
        {
            sidebar.style.display = "none"
            document.getElementsByClassName("content")[0].style.marginLeft="1rem" ;
        }
    else 
       {
        sidebar.style.display = "block"
        document.getElementsByClassName("content")[0].style.marginLeft="20%" ;
    }
         
}

document.getElementsByClassName("menu")[0].addEventListener("click",menufun)



const crossfun = () =>{
     document.getElementsByClassName("sidebar")[0].style.display="none";
     document.getElementsByClassName("content")[0].style.marginLeft="1rem" ;   
    
}

document.getElementsByClassName("cross-btn")[0].addEventListener("click",crossfun);

