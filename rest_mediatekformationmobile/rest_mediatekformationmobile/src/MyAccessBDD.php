<?php
include_once("AccessBDD.php");

/**
 * Classe de construction des requêtes SQL
 * hérite de AccessBDD qui contient les requêtes de base
 * Pour ajouter une requête :
 * - créer la fonction qui crée une requête (prendre modèle sur les fonctions 
 *   existantes qui ne commencent pas par 'traitement')
 * - ajouter un 'case' dans un des switch des fonctions redéfinies 
 * - appeler la nouvelle fonction dans ce 'case'
 */
class MyAccessBDD extends AccessBDD {
	    
    /**
     * constructeur qui appelle celui de la classe mère
     */
    public function __construct(){
        try{
            parent::__construct();
        }catch(\Exception $e){
            throw $e;
        }
    }

    /**
     * demande de recherche
     * @param string $table
     * @param array|null $champs nom et valeur de chaque champ
     * @return array|null tuples du résultat de la requête ou null si erreur
     * @override
     */	
    protected function traitementSelect(string $table, ?array $champs) : ?array{
        switch($table){     
            case "formations" :
                 return $this->selectMotCle($champs);      
            case "variantes" :
                return $this->selectVariantes();
            case "intolerance" :
                return $this->selectIntolerance($champs);
            case "" :
                // return $this->uneFonction(parametres);
            default:
                // cas général
                return $this->selectTuplesOneTable($table, $champs);
        }	
    }

    /**
     * demande d'ajout (insert)
     * @param string $table
     * @param array|null $champs nom et valeur de chaque champ
     * @return int|null nombre de tuples ajoutés ou null si erreur
     * @override
     */	
    protected function traitementInsert(string $table, ?array $champs) : ?int{
        switch($table){
            case "produit" :
                return $this->insertProduit($champs);
            case "" :
                // return $this->uneFonction(parametres);
            default:                    
                // cas général
                return $this->insertOneTupleOneTable($table, $champs);	
        }
    }
    
    /**
     * demande de modification (update)
     * @param string $table
     * @param string|null $id
     * @param array|null $champs nom et valeur de chaque champ
     * @return int|null nombre de tuples modifiés ou null si erreur
     * @override
     */	
    protected function traitementUpdate(string $table, ?string $id, ?array $champs) : ?int{
        switch($table){
            case "transfert_images" :
                return $this->updateCheminImges($champs);
            case "" :
                // return $this->uneFonction(parametres);
            default:                    
                // cas général
                return $this->updateOneTupleOneTable($table, $id, $champs);
        }	
    }  
    
    /**
     * demande de suppression (delete)
     * @param string $table
     * @param array|null $champs nom et valeur de chaque champ
     * @return int|null nombre de tuples supprimés ou null si erreur
     * @override
     */	
    protected function traitementDelete(string $table, ?array $champs) : ?int{
        switch($table){
            case "nettoie_gamme" :
                return $this->deleteNettoieGamme();
            case "" :
                // return $this->uneFonction(parametres);
            default:                    
                // cas général
                return $this->deleteTuplesOneTable($table, $champs);	
        }
    }	    
        
    /**
     * récupère les tuples d'une seule table
     * @param string $table
     * @param array|null $champs
     * @return array|null 
     */
    private function selectTuplesOneTable(string $table, ?array $champs) : ?array{
        if(empty($champs)){
            // tous les tuples d'une table
            $requete = "select * from $table;";
            return $this->conn->queryBDD($requete);  
        }else{
            // tuples spécifiques d'une table
            $requete = "select * from $table where ";
            foreach ($champs as $key => $value){
                $requete .= "$key=:$key and ";
            }
            // (enlève le dernier and)
            $requete = substr($requete, 0, strlen($requete)-5);	 
            return $this->conn->queryBDD($requete, $champs);
        }
    }	

    /**
     * demande d'ajout (insert) d'un tuple dans une table
     * @param string $table
     * @param array|null $champs
     * @return int|null nombre de tuples ajoutés (0 ou 1) ou null si erreur
     */	
    private function insertOneTupleOneTable(string $table, ?array $champs) : ?int{
        if(empty($champs)){
            return null;
        }
        // construction de la requête
        $requete = "insert into $table (";
        foreach ($champs as $key => $value){
            $requete .= "$key,";
        }
        // (enlève la dernière virgule)
        $requete = substr($requete, 0, strlen($requete)-1);
        $requete .= ") values (";
        foreach ($champs as $key => $value){
            $requete .= ":$key,";
        }
        // (enlève la dernière virgule)
        $requete = substr($requete, 0, strlen($requete)-1);
        $requete .= ");";
        return $this->conn->updateBDD($requete, $champs);
    }

    /**
     * demande de modification (update) d'un tuple dans une table
     * @param string $table
     * @param string\null $id
     * @param array|null $champs 
     * @return int|null nombre de tuples modifiés (0 ou 1) ou null si erreur
     */	
    private function updateOneTupleOneTable(string $table, ?string $id, ?array $champs) : ?int {
        if(empty($champs)){
            return null;
        }
        if(is_null($id)){
            return null;
        }
        // construction de la requête
        $requete = "update $table set ";
        foreach ($champs as $key => $value){
            $requete .= "$key=:$key,";
        }
        // (enlève la dernière virgule)
        $requete = substr($requete, 0, strlen($requete)-1);				
        $champs["id"] = $id;
        $requete .= " where id=:id;";		
        return $this->conn->updateBDD($requete, $champs);	        
    }
    
    /**
     * demande de suppression (delete) d'un ou plusieurs tuples dans une table
     * @param string $table
     * @param array|null $champs
     * @return int|null nombre de tuples supprimés ou null si erreur
     */
    private function deleteTuplesOneTable(string $table, ?array $champs) : ?int{
        if(empty($champs)){
            return null;
        }
        // construction de la requête
        $requete = "delete from $table where ";
        foreach ($champs as $key => $value){
            $requete .= "$key=:$key and ";
        }
        // (enlève le dernier and)
        $requete = substr($requete, 0, strlen($requete)-5);   
        return $this->conn->updateBDD($requete, $champs);	        
    }

    /**
     * Récupère les formations selon un mot clé
     * @param array|null $champs paramètres de recherche
     * @return array|null résultat de la requête
     */
    private function selectMotCle(?array $champs) : ?array {
        if (empty($champs) || !isset($champs['motcle'])) {
            // Si aucun mot-clé n'est fourni, retourne toutes les formations
            $requete = "SELECT f.id, f.published_at, f.title, f.description, f.video_id, f.playlist_id 
                        FROM formation f 
                        ORDER BY f.published_at DESC;";
            return $this->conn->queryBDD($requete);
        } else {
            // Recherche par mot-clé dans le titre
            $requete = "SELECT f.id, f.published_at, f.title, f.description, f.video_id, f.playlist_id 
                        FROM formation f 
                        WHERE f.title LIKE :motcle 
                        
                        ORDER BY f.published_at DESC;";
            $champs['motcle'] = '%' . $champs['motcle'] . '%';
            return $this->conn->queryBDD($requete, $champs);
        }
    }
    
    /**
     * Récupère les variantes des formations
     * @return array|null résultat de la requête
     */
    private function selectVariantes() : ?array {
        $requete = "SELECT f.id, f.published_at, f.title, f.description, f.video_id, f.playlist_id, 
                    p.name as playlist_name
                    FROM formation f 
                    INNER JOIN playlist p ON f.playlist_id = p.id 
                    ORDER BY f.published_at DESC;";
        return $this->conn->queryBDD($requete);
    }
    
    /**
     * Récupère les formations par catégorie
     * @param array|null $champs paramètres de recherche contenant l'ID de la catégorie
     * @return array|null résultat de la requête
     */
    private function selectIntolerance(?array $champs) : ?array {
        if(empty($champs) || !isset($champs['categorie'])) {
            // Si aucune catégorie n'est spécifiée, retourne toutes les formations
            $requete = "SELECT f.id, f.published_at, f.title, f.description, f.video_id, f.playlist_id 
                        FROM formation f 
                        ORDER BY f.published_at DESC;";
            return $this->conn->queryBDD($requete);
        } else {
            // Recherche par catégorie
            $requete = "SELECT f.id, f.published_at, f.title, f.description, f.video_id, f.playlist_id 
                        FROM formation f 
                        INNER JOIN formation_categorie fc ON f.id = fc.formation_id 
                        WHERE fc.categorie_id = :categorie 
                        ORDER BY f.published_at DESC;";
            return $this->conn->queryBDD($requete, $champs);
        }
    }
    
    /**
     * Insère un nouveau produit (formation)
     * @param array|null $champs informations du produit à ajouter
     * @return int|null nombre de tuples ajoutés ou null si erreur
     */
    private function insertProduit(?array $champs) : ?int {
        if(empty($champs)) {
            return null;
        }
        
        // Vérification de la présence des champs requis
        $requiredFields = ['title', 'description', 'playlist_id'];
        foreach($requiredFields as $field) {
            if(!isset($champs[$field])) {
                return null;
            }
        }
        
        // Construction de la requête d'insertion
        $requete = "INSERT INTO formation (title, description, playlist_id";
        
        // Ajout des champs facultatifs s'ils sont présents
        if(isset($champs['published_at'])) {
            $requete .= ", published_at";
        } else {
            $champs['published_at'] = date('Y-m-d H:i:s');
            $requete .= ", published_at";
        }
        
        if(isset($champs['video_id'])) {
            $requete .= ", video_id";
        }
        
        $requete .= ") VALUES (:title, :description, :playlist_id";
        
        // Ajout des valeurs pour les champs facultatifs
        if(isset($champs['published_at']) || !isset($champs['published_at'])) {
            $requete .= ", :published_at";
        }
        
        if(isset($champs['video_id'])) {
            $requete .= ", :video_id";
        }
        
        $requete .= ");";
        
        return $this->conn->updateBDD($requete, $champs);
    }
    
    /**
     * Met à jour les chemins d'images (video_id) pour les formations
     * @param array|null $champs contient l'ancien et le nouveau chemin
     * @return int|null nombre de tuples modifiés ou null si erreur
     */
    private function updateCheminImges(?array $champs) : ?int {
        if(empty($champs) || !isset($champs['ancien_chemin']) || !isset($champs['nouveau_chemin'])) {
            return null;
        }
        
        $requete = "UPDATE formation 
                    SET video_id = REPLACE(video_id, :ancien_chemin, :nouveau_chemin) 
                    WHERE video_id LIKE :pattern;";
        
        $parametres = [
            'ancien_chemin' => $champs['ancien_chemin'],
            'nouveau_chemin' => $champs['nouveau_chemin'],
            'pattern' => '%' . $champs['ancien_chemin'] . '%'
        ];
        
        return $this->conn->updateBDD($requete, $parametres);
    }
    
    /**
     * Supprime les formations qui ne sont pas associées à une playlist
     * @return int|null nombre de tuples supprimés ou null si erreur
     */
    private function deleteNettoieGamme() : ?int {
        $requete = "DELETE FROM formation 
                    WHERE playlist_id IS NULL;";
        
        return $this->conn->updateBDD($requete, []);
    }
}