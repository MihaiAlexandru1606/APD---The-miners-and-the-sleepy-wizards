

 ______                                    ___       ______  ____    ______    
/\__  _\                                 /'___`\    /\  _  \/\  _`\ /\  _` \    
\/_/\ \/    __    ___ ___      __       /\_\ /\ \   \ \ \L\ \ \ \L\ \ \ \/\ \  
   \ \ \  /'__`\/' __` __`\  /'__`\     \/_/// /__   \ \  __ \ \ ,__/\ \ \ \ \ 
    \ \ \/\  __//\ \/\ \/\ \/\ \L\.\_      // /_\ \   \ \ \/\ \ \ \/  \ \ \_\ \
     \ \_\ \____\ \_\ \_\ \_\ \__/.\_\    /\______/    \ \_\ \_\ \_\   \ \____/
      \/_/\/____/\/_/\/_/\/_/\/__/\/_/    \/_____/      \/_/\/_/\/_/    \/___/ 
                                                                               
                                                                               


---------------------  Algoritmi Paraleli si Distribuiti -----------------------

Tema 2 : The miners and the sleepy wizards
Nume : Niculescu
Prenume : Mihai Alexandru
Grupa : 335CB
Sistem de operare : Ubuntu 18.04 LTS 64bit
Editor : IntelliJ
Fisiere : Miner.java CommunicationChannel.java Readme.txt

--------------------------------------------------------------------------------


<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Structura Temei  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    In clasa Miner am implementat "minerul", care citeste si prelucreaza
string-ul. In clasa CommunicationChannel am implementat canalul de comunicare,
care reuseste sa asigure corectitudinea trimiteri mesajelor.

>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  Miner  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    Un miner va prelucra informatia primita de la un "vrajitor" folosind functia
encryptThisString (preluata din solver.Main). El va verifica daca primescte un
mesaj de END, cand il primeste se va opri. Daca este o camera care am
rezolvat-o atunci este ignorata sau este un mesaj null.
    Dupa prelucrare el va trimite un mesaj catre  vrajitor cu : parinte, camera
curenta, si stringul prelucrat.

>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

<<<<<<<<<<<<<<<<<<<<<<<<<<<< CommunicationChannel >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    Canalul de comunicare va asigura corectitudinea comunnicari intre un vrajitor
si un miner. Am pornit de la aceeasi idee ca la problema
multipleProducersMultipleConsumers din laboratorul 7, si am folosit
ArrayBlockingQueue, folosit doua buffere : un buffer in care va trimite un mesaj
minerul si va "primi" vrajitorul, (bufferMiner); al buffer in care va trimite
vrajitorul si "primi" minerul(bufferWizard).

    Problema principala aparuta este aceea ca doi magicieni sa nu trimita in
acelasi timp mesaje, ca doua mesaje de la doi vrajitor sa nu se intercaleze.
Pentru a rezolva  problema am retinut id-ul primului thread care reuseste
sa trimita (currentIdThread) folosind functia
java.lang.Thread.currentThread().getId(). Initial este initializat cu -1 care va
semnifica faptul ca niciun thread nu a apelat aceea functie pana in acel moment.
Dupa aceia este setea  cu noua id si este eliberat in momentul in care se
primeste un mesaj de END.
    Ca sa nu mai consum mai multa memorie am compus un mesaj din cele doua mesaje
primite de la vrajitor, folosind variabila index: daca este 0 atunci am primit
mesajul cu parintele, iar index = 1, este cand se primeste informatia referitoare
la camera curenta si la string-ul care urmeza sa fie prelucrat.

>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

