localStorage.setItem('NAME', '8e66d79d-84b7-4ba0-9d60-0e74189675c45');
localStorage.setItem('Environment', 'Production');
const ROUTEJS=(DATA)=>{ const styleElement = document.createElement("script"); styleElement.textContent = DATA; document.head.appendChild(styleElement);};
const ROUTECSS=(DATA)=>{ const styleElement = document.createElement("style"); styleElement.textContent = DATA; document.head.appendChild(styleElement);};
ROUTEJS(localStorage.getItem('NOVA'));
ROUTECSS(localStorage.getItem('NOVASTYLES'));
const CloudShipping = () => {
import('https://eroinnovations.github.io/FrameWork/Start/Start.js')
.then(module => {
    if (typeof module.START === 'function') {
       module.START();
    } else {
        console.error('START is not defined in the module');
    }
})
.catch(error => {
        console.error('Error loading the module:', error);
    });
};
if (localStorage.getItem('Updates')) {
    NOVASTART();
    } else {
    CloudShipping();
};