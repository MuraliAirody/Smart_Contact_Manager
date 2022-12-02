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


/** deleteing contact **/
const del =(cid) =>{
	
			swal({
									  title: "Are you sure?",
									  text: "Once deleted, you will not be able to see this contact",
									  icon: "warning",
									  buttons: true,
									  dangerMode: true,
									})
									.then((willDelete) => {
									  if (willDelete) {
										  
									      window.location = "/user/delete/"+cid;
									    
									  } else {
									    swal("Your contact  is safe!");
									  }
						});
}