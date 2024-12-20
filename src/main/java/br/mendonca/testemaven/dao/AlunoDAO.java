package br.mendonca.testemaven.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.mendonca.testemaven.model.entities.Aluno;

public class AlunoDAO {

    public void register(Aluno aluno) throws ClassNotFoundException, SQLException {
        Connection conn = ConnectionPostgres.getConexao();
        conn.setAutoCommit(true);

        PreparedStatement ps = conn.prepareStatement("INSERT INTO alunos (uuid, nome, media, deletado, isAtivo) VALUES (?, ?, ?, ?, ?)");
        ps.setObject(1, UUID.randomUUID());
        ps.setString(2, aluno.getNome());
        ps.setDouble(3, aluno.getMedia());
        ps.setBoolean(4, aluno.isDeletado());
        ps.setBoolean(5, aluno.isAtivo());
        ps.execute();
        ps.close();
    }

    public List<Aluno> listAllAlunos() throws ClassNotFoundException, SQLException {
        List<Aluno> lista = new ArrayList<>();

        Connection conn = ConnectionPostgres.getConexao();
        conn.setAutoCommit(true);

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM alunos ORDER BY data_criacao DESC");

        while (rs.next()) {
            Aluno aluno = new Aluno();
            aluno.setUuid((UUID) rs.getObject("uuid"));
            aluno.setNome(rs.getString("nome"));
            aluno.setMedia(rs.getDouble("media"));
            aluno.setAtivo(rs.getBoolean("isAtivo"));

            lista.add(aluno);
        }

        rs.close();
        st.close();

        return lista;
    }

    public Aluno searchByNome(String nome) throws ClassNotFoundException, SQLException {
        Aluno aluno = null;

        Connection conn = ConnectionPostgres.getConexao();
        conn.setAutoCommit(true);

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM alunos WHERE LOWER(nome) LIKE LOWER(?)");
        ps.setString(1, nome + "%");

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            aluno = new Aluno();
            aluno.setUuid((UUID) rs.getObject("uuid"));
            aluno.setNome(rs.getString("nome"));
            aluno.setMedia(rs.getDouble("media"));
            aluno.setAtivo(rs.getBoolean("isAtivo"));
        }

        rs.close();
        ps.close();

        return aluno;
    }

    public Aluno searchById(UUID uuid) throws ClassNotFoundException, SQLException {
        Aluno aluno = null;

        Connection conn = ConnectionPostgres.getConexao();
        conn.setAutoCommit(true);

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM alunos WHERE uuid = ?");
        ps.setObject(1, uuid);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            aluno = new Aluno();
            aluno.setUuid((UUID) rs.getObject("uuid"));
            aluno.setNome(rs.getString("nome"));
            aluno.setMedia(rs.getDouble("media"));
            aluno.setAtivo(rs.getBoolean("isAtivo"));
        }

        rs.close();
        ps.close();

        return aluno;
    }

    public List<Aluno> listAlunosPaginated(int pageNumber, int pageSize, boolean deletado) throws ClassNotFoundException, SQLException {
        List<Aluno> lista = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;

        Connection conn = ConnectionPostgres.getConexao();
        conn.setAutoCommit(true);
        String sql = "SELECT * FROM alunos WHERE deletado = ? ORDER BY nome LIMIT ? OFFSET ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setBoolean(1, deletado);
        ps.setInt(2, pageSize);
        ps.setInt(3, offset);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Aluno aluno = new Aluno();
            aluno.setUuid((UUID) rs.getObject("uuid"));
            aluno.setNome(rs.getString("nome"));
            aluno.setMedia(rs.getDouble("media"));
            aluno.setAtivo(rs.getBoolean("isAtivo"));

            lista.add(aluno);
        }

        rs.close();
        ps.close();

        return lista;
    }

    public int countAllAlunos(boolean deletado) throws ClassNotFoundException, SQLException {
        int count = 0;

        Connection conn = ConnectionPostgres.getConexao();
        conn.setAutoCommit(true);

        String sql = "SELECT COUNT(*) FROM alunos WHERE deletado = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setBoolean(1, deletado);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }

        rs.close();
        ps.close();

        return count;
    }

    public void deleteByUuid(UUID uuid) throws ClassNotFoundException, SQLException {
        Connection conn = ConnectionPostgres.getConexao();
        conn.setAutoCommit(true);

        String sql = "UPDATE alunos SET deletado = true WHERE uuid = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1, uuid);

        ps.executeUpdate();
        ps.close();
    }

}
